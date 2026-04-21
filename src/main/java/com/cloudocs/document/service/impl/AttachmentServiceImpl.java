package com.cloudocs.document.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudocs.document.entity.Attachment;
import com.cloudocs.document.mapper.AttachmentMapper;
import com.cloudocs.document.service.AttachmentService;
import com.cloudocs.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AttachmentServiceImpl extends ServiceImpl<AttachmentMapper, Attachment> implements AttachmentService {

    @Override
    @Transactional
    public Attachment uploadAttachment(Attachment attachment) {
        attachment.setUploaderId(UserContext.getUserId());
        if (attachment.getUploaderName() == null) {
            attachment.setUploaderName(UserContext.getUser() != null ? UserContext.getUser().getNickname() : null);
        }
        attachment.setDeleted(0);
        boolean saved = this.save(attachment);
        if (!saved) {
            throw new RuntimeException("保存附件失败，save返回false");
        }
        return attachment;
    }

    @Override
    public List<Attachment> getAttachmentsByDocumentId(Long documentId) {
        LambdaQueryWrapper<Attachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attachment::getDocumentId, documentId)
               .eq(Attachment::getDeleted, 0)
               .orderByDesc(Attachment::getCreatedAt);
        return this.list(wrapper);
    }

    @Override
    @Transactional
    public void deleteAttachment(Long id) {
        com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<Attachment> wrapper =
            new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
        wrapper.eq("id", id).set("deleted", 1);
        this.update(wrapper);
    }
}
