package com.cloudocs.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudocs.security.JwtUtils;
import com.cloudocs.security.UserContext;
import com.cloudocs.tenant.TenantContextHolder;
import com.cloudocs.user.entity.User;
import com.cloudocs.user.mapper.UserMapper;
import com.cloudocs.user.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    @Transactional
    public void register(User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername())
               .eq(User::getTenantId, user.getTenantId());
        if (this.count(wrapper) > 0) {
            throw new IllegalArgumentException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        this.save(user);
    }

    @Override
    public String login(String username, String password, Long tenantId) {
        // 设置租户上下文，让查询使用正确的 tenantId
        TenantContextHolder.setTenantId(tenantId);
        try {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, username)
                   .eq(User::getTenantId, tenantId);
            User user = this.getOne(wrapper);

            if (user == null) {
                throw new IllegalArgumentException("用户不存在");
            }

            if (user.getStatus() == 0) {
                throw new IllegalArgumentException("用户已被禁用");
            }

            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new IllegalArgumentException("密码错误");
            }

            return jwtUtils.generateToken(user.getId(), tenantId, user.getRole());
        } finally {
            TenantContextHolder.clear();
        }
    }

    @Override
    public User getUserById(Long id) {
        return this.getById(id);
    }

    @Override
    public User getUserByUsername(String username, Long tenantId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
               .eq(User::getTenantId, tenantId);
        return this.getOne(wrapper);
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        User existing = this.getById(user.getId());
        if (existing == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        user.setPassword(null);
        this.updateById(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        this.removeById(id);
    }

    @Override
    public Page<User> listUsers(Long tenantId, Integer page, Integer size) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getTenantId, tenantId);
        Page<User> pager = new Page<>(page, size);
        return this.page(pager, wrapper);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = this.getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(user);
    }

    @Override
    public List<User> getUsersByTenant() {
        Long tenantId = TenantContextHolder.getTenantId();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getTenantId, tenantId);
        return this.list(wrapper);
    }

    @Override
    @Transactional
    public void inviteUser(User user) {
        Long tenantId = TenantContextHolder.getTenantId();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername())
               .eq(User::getTenantId, tenantId);
        if (this.count(wrapper) > 0) {
            throw new IllegalArgumentException("用户名已存在");
        }
        user.setTenantId(tenantId);
        user.setPassword(passwordEncoder.encode("123456")); // 默认密码
        user.setStatus(1);
        this.save(user);
    }

    @Override
    @Transactional
    public void enableUser(Long id) {
        User user = this.getById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        user.setStatus(1);
        this.updateById(user);
    }

    @Override
    @Transactional
    public void disableUser(Long id) {
        User user = this.getById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        user.setStatus(0);
        this.updateById(user);
    }
}
