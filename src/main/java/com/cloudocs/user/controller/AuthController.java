package com.cloudocs.user.controller;

import com.cloudocs.common.Result;
import com.cloudocs.security.UserContext;
import com.cloudocs.sms.service.SmsService;
import com.cloudocs.user.entity.User;
import com.cloudocs.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "用户认证")
public class AuthController {

    private final UserService userService;
    private final SmsService smsService;

    public AuthController(UserService userService, SmsService smsService) {
        this.userService = userService;
        this.smsService = smsService;
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
        String token = userService.login(request.getUsername(), request.getPassword());
        return Result.success(token);
    }

    // ========== 手机号验证码相关 ==========

    @PostMapping("/send-code")
    @Operation(summary = "发送验证码")
    public Result<Void> sendCode(@RequestParam String phone, @RequestParam String type) {
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            return Result.error("请输入正确的手机号");
        }
        smsService.sendCode(phone, type);
        return Result.success();
    }

    @PostMapping("/register-by-phone")
    @Operation(summary = "手机号验证码注册")
    public Result<Map<String, Object>> registerByPhone(@RequestBody PhoneRegisterRequest request) {
        // 验证验证码
        if (!smsService.verifyCode(request.getPhone(), request.getCode(), "REGISTER")) {
            return Result.error("验证码无效或已过期");
        }

        // 生成邮箱：用户名@Xmail.com
        String email = request.getUsername() + "@Xmail.com";

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setEmail(email);
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());

        userService.register(user);

        // 标记验证码已使用
        smsService.markAsUsed(request.getPhone(), request.getCode(), "REGISTER");

        // 自动登录
        String token = userService.login(request.getUsername(), request.getPassword());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("email", email);
        return Result.success(result);
    }

    @PostMapping("/login-by-phone")
    @Operation(summary = "手机号验证码登录")
    public Result<Map<String, Object>> loginByPhone(@RequestBody PhoneLoginRequest request) {
        // 验证验证码
        if (!smsService.verifyCode(request.getPhone(), request.getCode(), "LOGIN")) {
            return Result.error("验证码无效或已过期");
        }

        // 根据手机号获取用户
        User user = userService.getByPhone(request.getPhone());
        if (user == null) {
            return Result.error("该手机号未注册");
        }

        // 标记验证码已使用
        smsService.markAsUsed(request.getPhone(), request.getCode(), "LOGIN");

        // 生成token
        String token = userService.loginByUserId(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return Result.success(result);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "通过验证码重置密码")
    public Result<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        // 验证验证码
        if (!smsService.verifyCode(request.getPhone(), request.getCode(), "FORGOT")) {
            return Result.error("验证码无效或已过期");
        }

        // 根据手机号获取用户
        User user = userService.getByPhone(request.getPhone());
        if (user == null) {
            return Result.error("该手机号未注册");
        }

        // 重置密码
        userService.resetPassword(user.getId(), request.getNewPassword());

        // 标记验证码已使用
        smsService.markAsUsed(request.getPhone(), request.getCode(), "FORGOT");

        return Result.success();
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

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class PhoneRegisterRequest {
        private String phone;
        private String code;
        private String username;
        private String password;
        private String nickname;

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
    }

    public static class PhoneLoginRequest {
        private String phone;
        private String code;

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
    }

    public static class ResetPasswordRequest {
        private String phone;
        private String code;
        private String newPassword;

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
