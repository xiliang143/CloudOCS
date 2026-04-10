package com.cloudocs.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudocs.user.entity.User;

import java.util.List;

public interface UserService extends IService<User> {

    /**
     * 用户注册（需指定租户）
     */
    void register(User user);

    /**
     * 用户登录，返回JWT token
     */
    String login(String username, String password, Long tenantId);

    /**
     * 根据ID获取
     */
    User getUserById(Long id);

    /**
     * 根据用户名和租户获取
     */
    User getUserByUsername(String username, Long tenantId);

    /**
     * 更新用户
     */
    void updateUser(User user);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 获取租户下所有用户（分页）
     */
    Page<User> listUsers(Long tenantId, Integer page, Integer size);

    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 获取当前租户下所有用户
     */
    List<User> getUsersByTenant();

    /**
     * 邀请用户（创建新用户）
     */
    void inviteUser(User user);

    /**
     * 启用用户
     */
    void enableUser(Long id);

    /**
     * 禁用用户
     */
    void disableUser(Long id);
}
