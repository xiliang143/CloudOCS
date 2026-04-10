package com.cloudocs.document.share.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.cloudocs.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("shares")
public class Share extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文档ID
     */
    private Long documentId;

    /**
     * 分享Token（UUID）
     */
    private String token;

    /**
     * 访问类型：1-租户内，2-跨租户
     */
    private Integer accessType;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 创建者ID
     */
    private Long creatorId;

    /**
     * 浏览次数
     */
    private Integer viewCount;
}
