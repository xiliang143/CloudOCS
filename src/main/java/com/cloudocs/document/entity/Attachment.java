package com.cloudocs.document.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.cloudocs.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文档附件实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("document_attachments")
public class Attachment extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文档ID
     */
    private Long documentId;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件路径
     */
    private String url;

    /**
     * 文件大小（字节）
     */
    private Long size;

    /**
     * 文件类型
     */
    private String contentType;

    /**
     * 上传者ID
     */
    private Long uploaderId;

    /**
     * 上传者名称
     */
    private String uploaderName;

    /**
     * 是否删除：0-正常，1-已删除
     */
    private Integer deleted;
}
