package com.cloudocs.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.cloudocs.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("users")
public class User extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 所属租户ID
     */
    private Long tenantId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（BCrypt加密）
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机
     */
    private String phone;

    /**
     * 角色：1-租户管理员，2-编辑，3-查看者
     */
    private Integer role;

    /**
     * 状态：1-正常，0-禁用
     */
    private Integer status;
}
