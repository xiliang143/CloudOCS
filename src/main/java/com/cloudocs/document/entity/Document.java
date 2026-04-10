package com.cloudocs.document.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.cloudocs.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("documents")
public class Document extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 父文件夹ID，null表示根目录
     */
    private Long parentId;

    /**
     * 文档标题
     */
    private String title;

    /**
     * 文档内容（富文本HTML）
     */
    private String content;

    /**
     * 类型：1-文件夹，2-文档
     */
    private Integer type;

    /**
     * 创建者ID
     */
    private Long creatorId;

    /**
     * 创建者名称
     */
    private String creatorName;

    /**
     * 是否删除：0-正常，1-已删除
     */
    private Integer isDeleted;
}
