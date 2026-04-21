package com.cloudocs.document.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudocs.document.entity.Attachment;

import java.util.List;

public interface AttachmentService extends IService<Attachment> {

    /**
     * 上传附件
     */
    Attachment uploadAttachment(Attachment attachment);

    /**
     * 获取文档的所有附件
     */
    List<Attachment> getAttachmentsByDocumentId(Long documentId);

    /**
     * 删除附件
     */
    void deleteAttachment(Long id);
}
