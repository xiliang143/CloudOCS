package com.cloudocs.tenant.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.cloudocs.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tenants")
public class Tenant extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户名称
     */
    private String name;

    /**
     * 租户编码（唯一）
     */
    private String code;

    /**
     * 状态：0-待审核，1-启用，2-禁用
     */
    private Integer status;

    /**
     * 套餐：free/professional/enterprise
     */
    private String packageType;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 联系人
     */
    private String contact;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 备注
     */
    private String remark;
}
