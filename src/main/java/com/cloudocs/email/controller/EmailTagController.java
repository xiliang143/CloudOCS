package com.cloudocs.email.controller;

import com.cloudocs.common.Result;
import com.cloudocs.email.entity.EmailTag;
import com.cloudocs.email.service.EmailTagService;
import com.cloudocs.security.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/email/tags")
@Tag(name = "邮箱标签")
public class EmailTagController {

    private final EmailTagService emailTagService;

    public EmailTagController(EmailTagService emailTagService) {
        this.emailTagService = emailTagService;
    }

    @GetMapping
    @Operation(summary = "获取标签列表")
    public Result<List<EmailTag>> getTags() {
        Long userId = UserContext.getUserId();
        List<EmailTag> tags = emailTagService.getTagsByUserId(userId);
        return Result.success(tags);
    }

    @PostMapping
    @Operation(summary = "创建标签")
    public Result<EmailTag> createTag(@RequestBody EmailTag tag) {
        tag.setCreatorId(UserContext.getUserId());
        emailTagService.save(tag);
        return Result.success(tag);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除标签")
    public Result<Void> deleteTag(@PathVariable Long id) {
        emailTagService.removeById(id);
        return Result.success();
    }
}
