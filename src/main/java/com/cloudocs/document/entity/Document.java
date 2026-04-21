package com.cloudocs.document.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.cloudocs.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 文档实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("documents")
public class Document extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 所属文件夹ID，0表示根目录
     */
    private Long folderId;

    /**
     * 文档标题
     */
    private String title;

    /**
     * 文档内容（富文本HTML）
     */
    private String content;

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
    private Integer deleted;

    // ========== 邮箱相关字段 ==========

    /**
     * 邮件类型：INBOX/SENT/DRAFT/ARCHIVE/SPAM/TRASH
     */
    private String mailboxType;

    /**
     * 优先级：1-低/2-普通/3-高/4-紧急
     */
    private Integer priority;

    /**
     * 星标：0-否/1-是
     */
    private Integer starred;

    /**
     * 标签（JSON数组）
     */
    private String tags;

    /**
     * 发件人ID
     */
    private Long fromUserId;

    /**
     * 发件人名称
     */
    private String fromUserName;

    /**
     * 收件人ID列表（JSON）
     */
    private String toUserIds;

    /**
     * 收件人名称列表
     */
    private String toUserNames;

    /**
     * 抄送人ID列表（JSON）
     */
    private String ccUserIds;

    /**
     * 已读：0-否/1-是
     */
    private Integer isRead;

    /**
     * 发件人删除标记：0-未删除/1-已删除
     */
    private Integer senderDeleted;
}
