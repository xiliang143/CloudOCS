package com.cloudocs.document.version.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.cloudocs.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("document_versions")
public class DocumentVersion extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文档ID
     */
    private Long documentId;

    /**
     * 版本内容（快照）
     */
    private String content;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 创建者ID
     */
    private Long creatorId;

    /**
     * 创建者名称
     */
    private String creatorName;

    /**
     * 版本备注
     */
    private String remark;
}
