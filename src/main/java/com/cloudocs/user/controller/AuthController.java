package com.cloudocs.user.controller;

import com.cloudocs.common.Result;
import com.cloudocs.security.UserContext;
import com.cloudocs.user.entity.User;
import com.cloudocs.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "用户认证")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<Void> register(@RequestBody User user) {
        userService.register(user);
        return Result.success();
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<String> login(@RequestBody LoginRequest request) {
        String token = userService.login(request.getUsername(), request.getPassword(), request.getTenantId());
        return Result.success(token);
    }

    public static class LoginRequest {
        private String username;
        private String password;
        private Long tenantId;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public Long getTenantId() { return tenantId; }
        public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息")
    public Result<User> getCurrentUser() {
        User user = UserContext.getUser();
        return Result.success(user);
    }

    @PostMapping("/change-password")
    @Operation(summary = "修改密码")
    public Result<Void> changePassword(@RequestParam String oldPassword,
                                        @RequestParam String newPassword) {
        Long userId = UserContext.getUserId();
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.success();
    }
}
