package com.cloudocs.document.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudocs.document.entity.Document;
import com.cloudocs.document.mapper.DocumentMapper;
import com.cloudocs.document.service.DocumentService;
import com.cloudocs.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements DocumentService {

    @Override
    @Transactional
    public void createDocument(Document document) {
        document.setCreatorId(UserContext.getUserId());
        if (document.getCreatorName() == null) {
            document.setCreatorName(UserContext.getUser() != null ? UserContext.getUser().getNickname() : null);
        }
        document.setIsDeleted(0);
        if (document.getParentId() == null) {
            document.setParentId(0L);
        }
        this.save(document);
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
        this.updateById(document);
    }

    @Override
    @Transactional
    public void deleteDocument(Long id) {
        Document document = this.getById(id);
        if (document == null) {
            throw new IllegalArgumentException("文档不存在");
        }
        document.setIsDeleted(1);
        this.updateById(document);
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
        document.setIsDeleted(0);
        this.updateById(document);
    }

    @Override
    public Document getDocumentById(Long id) {
        return this.getById(id);
    }

    @Override
    public List<Document> getDocumentsByFolderId(Long parentId) {
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Document::getParentId, parentId)
               .eq(Document::getIsDeleted, 0)
               .orderByAsc(Document::getType)
               .orderByAsc(Document::getId);
        return this.list(wrapper);
    }

    @Override
    public List<Document> getDocumentTree() {
        // 获取根目录下的所有顶级文档（排除已删除的）
        return getDocumentsByFolderId(0L);
    }

    @Override
    @Transactional
    public void moveDocument(Long id, Long targetFolderId) {
        Document document = this.getById(id);
        if (document == null) {
            throw new IllegalArgumentException("文档不存在");
        }
        document.setParentId(targetFolderId);
        this.updateById(document);
    }

    @Override
    public List<Document> getDeletedDocuments() {
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Document::getIsDeleted, 1)
               .orderByDesc(Document::getUpdatedAt);
        return this.list(wrapper);
    }

    /**
     * 递归获取树形结构（内部使用）
     */
    private List<Document> getTreeRecursive(Long parentId) {
        List<Document> result = new ArrayList<>();
        List<Document> children = getDocumentsByFolderId(parentId);

        for (Document child : children) {
            result.add(child);
            if (child.getType() != null && child.getType() == 1) {
                // 文件夹类型，递归获取子节点
                List<Document> grandchildren = getTreeRecursive(child.getId());
                result.addAll(grandchildren);
            }
        }
        return result;
    }
}
