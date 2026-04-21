package com.cloudocs.document.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudocs.document.entity.Document;
import com.cloudocs.document.mapper.DocumentMapper;
import com.cloudocs.document.service.DocumentService;
import com.cloudocs.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements DocumentService {

    @Override
    @Transactional
    public Document createDocument(Document document) {
        // 设置默认值
        if (document.getTitle() == null || document.getTitle().trim().isEmpty()) {
            document.setTitle("未命名文档");
        }
        if (document.getFolderId() == null) {
            document.setFolderId(0L);
        }
        document.setCreatorId(UserContext.getUserId());
        if (document.getCreatorName() == null) {
            document.setCreatorName(UserContext.getUser() != null ? UserContext.getUser().getNickname() : null);
        }
        document.setDeleted(0);
        this.save(document);
        return document;
    }

    @Override
    @Transactional
    public void updateDocument(Document document) {
        if (document.getId() == null) {
            throw new IllegalArgumentException("文档ID不能为空");
        }
        Document existing = this.getById(document.getId());
        if (existing == null) {
            throw new IllegalArgumentException("文档不存在");
        }
        // 使用 UpdateWrapper 只更新必要的字段，避免 null 覆盖
        com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<Document> wrapper =
            new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
        wrapper.eq("id", document.getId());
        if (document.getTitle() != null) {
            wrapper.set("title", document.getTitle());
        }
        if (document.getContent() != null) {
            wrapper.set("content", document.getContent());
        }
        if (document.getFolderId() != null) {
            wrapper.set("folder_id", document.getFolderId());
        }
        this.update(wrapper);
    }

    @Override
    @Transactional
    public void deleteDocument(Long id) {
        Document document = this.getById(id);
        if (document == null) {
            throw new IllegalArgumentException("文档不存在");
        }
        // 使用 UpdateWrapper 只更新 deleted 字段
        com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<Document> wrapper =
            new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
        wrapper.eq("id", id).set("deleted", 1);
        this.update(wrapper);
    }

    @Override
    @Transactional
    public void permanentDelete(Long id) {
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Document::getId, id);
        this.remove(wrapper);
    }

    @Override
    @Transactional
    public void restoreDocument(Long id) {
        Document document = this.getById(id);
        if (document == null) {
            throw new IllegalArgumentException("文档不存在");
        }
        // 使用 UpdateWrapper 只更新 deleted 字段
        com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<Document> wrapper =
            new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
        wrapper.eq("id", id).set("deleted", 0);
        this.update(wrapper);
    }

    @Override
    public Document getDocumentById(Long id) {
        return this.getById(id);
    }

    @Override
    public List<Document> getDocumentsByFolderId(Long folderId) {
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Document::getFolderId, folderId)
               .eq(Document::getDeleted, 0)
               .orderByAsc(Document::getId);
        return this.list(wrapper);
    }

    @Override
    @Transactional
    public void moveDocument(Long id, Long targetFolderId) {
        Document document = this.getById(id);
        if (document == null) {
            throw new IllegalArgumentException("文档不存在");
        }
        // 使用 UpdateWrapper 只更新 folder_id 字段
        com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<Document> wrapper =
            new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
        wrapper.eq("id", id).set("folder_id", targetFolderId);
        this.update(wrapper);
    }

    @Override
    public List<Document> getDeletedDocuments() {
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Document::getDeleted, 1)
               .orderByDesc(Document::getUpdatedAt);
        return this.list(wrapper);
    }

    @Override
    @Transactional
    public void emptyTrash() {
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Document::getDeleted, 1);
        this.remove(wrapper);
    }

    // ========== 邮箱功能 ==========

    @Override
    public List<Document> getEmailsByType(String type) {
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        Long userId = UserContext.getUserId();

        switch (type) {
            case "INBOX":
                wrapper.eq(Document::getMailboxType, "INBOX")
                       .eq(Document::getDeleted, 0)
                       .eq(Document::getSenderDeleted, 0);
                break;
            case "SENT":
                wrapper.eq(Document::getMailboxType, "SENT")
                       .eq(Document::getFromUserId, userId)
                       .eq(Document::getDeleted, 0);
                break;
            case "DRAFT":
                wrapper.eq(Document::getMailboxType, "DRAFT")
                       .eq(Document::getFromUserId, userId)
                       .eq(Document::getDeleted, 0);
                break;
            case "ARCHIVE":
                wrapper.eq(Document::getMailboxType, "ARCHIVE")
                       .eq(Document::getFromUserId, userId)
                       .eq(Document::getDeleted, 0);
                break;
            case "TRASH":
                wrapper.eq(Document::getDeleted, 1);
                break;
            default:
                wrapper.eq(Document::getMailboxType, type);
        }
        wrapper.orderByDesc(Document::getUpdatedAt);
        return this.list(wrapper);
    }

    @Override
    @Transactional
    public void setPriority(Long id, Integer priority) {
        com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<Document> wrapper =
            new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
        wrapper.eq("id", id).set("priority", priority);
        this.update(wrapper);
    }

    @Override
    @Transactional
    public void setStar(Long id, Integer starred) {
        com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<Document> wrapper =
            new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
        wrapper.eq("id", id).set("starred", starred);
        this.update(wrapper);
    }

    @Override
    @Transactional
    public void setTags(Long id, String tags) {
        com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<Document> wrapper =
            new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
        wrapper.eq("id", id).set("tags", tags);
        this.update(wrapper);
    }

    @Override
    @Transactional
    public void markRead(Long id, Integer isRead) {
        if (isRead == null) {
            isRead = 1;
        }
        com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<Document> wrapper =
            new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
        wrapper.eq("id", id).set("is_read", isRead);
        this.update(wrapper);
    }

    @Override
    @Transactional
    public void sendEmail(Long id) {
        com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<Document> wrapper =
            new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
        wrapper.eq("id", id).set("mailbox_type", "SENT").set("is_read", 1);
        this.update(wrapper);
    }

    @Override
    public int getUnreadCount() {
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Document::getMailboxType, "INBOX")
               .eq(Document::getDeleted, 0)
               .eq(Document::getSenderDeleted, 0)
               .eq(Document::getIsRead, 0);
        return (int) this.count(wrapper);
    }

    @Override
    @Transactional
    public Document createEmail(Document document) {
        if (document.getTitle() == null || document.getTitle().trim().isEmpty()) {
            document.setTitle("无主题");
        }
        document.setFromUserId(UserContext.getUserId());
        document.setFromUserName(UserContext.getUser() != null ? UserContext.getUser().getNickname() : null);
        document.setDeleted(0);
        document.setIsRead(1);
        if (document.getPriority() == null) {
            document.setPriority(2);
        }
        if (document.getStarred() == null) {
            document.setStarred(0);
        }
        this.save(document);
        return document;
    }
}
