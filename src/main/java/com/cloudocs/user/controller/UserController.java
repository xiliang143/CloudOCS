package com.cloudocs.user.controller;

import com.cloudocs.common.Result;
import com.cloudocs.security.UserContext;
import com.cloudocs.user.entity.User;
import com.cloudocs.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "用户列表")
    public Result<List<User>> listUsers() {
        return Result.success(userService.getAllUsers());
    }

    @PostMapping("/invite")
    @Operation(summary = "邀请用户")
    public Result<Void> inviteUser(@RequestBody User user) {
        userService.inviteUser(user);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.updateUser(user);
        return Result.success();
    }

    @PutMapping("/{id}/enable")
    @Operation(summary = "启用用户")
    public Result<Void> enableUser(@PathVariable Long id) {
        userService.enableUser(id);
        return Result.success();
    }

    @PutMapping("/{id}/disable")
    @Operation(summary = "禁用用户")
    public Result<Void> disableUser(@PathVariable Long id) {
        userService.disableUser(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }
}
