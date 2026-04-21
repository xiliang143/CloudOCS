package com.cloudocs.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudocs.user.entity.User;

import java.util.List;

public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    void register(User user);

    /**
     * 用户登录，返回JWT token
     */
    String login(String username, String password);

    /**
     * 根据ID获取
     */
    User getUserById(Long id);

    /**
     * 根据用户名获取
     */
    User getUserByUsername(String username);

    /**
     * 更新用户
     */
    void updateUser(User user);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 获取所有用户（分页）
     */
    Page<User> listUsers(Integer page, Integer size);

    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 获取所有用户
     */
    List<User> getAllUsers();

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

    /**
     * 根据手机号获取用户
     */
    User getByPhone(String phone);

    /**
     * 根据用户ID登录（内部使用）
     */
    String loginByUserId(Long userId);

    /**
     * 重置密码
     */
    void resetPassword(Long userId, String newPassword);
}
