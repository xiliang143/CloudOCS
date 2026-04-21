package com.cloudocs.email.controller;

import com.cloudocs.common.Result;
import com.cloudocs.email.entity.EmailFolder;
import com.cloudocs.email.service.EmailFolderService;
import com.cloudocs.security.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/email/folders")
@Tag(name = "邮箱文件夹")
public class EmailFolderController {

    private final EmailFolderService emailFolderService;

    public EmailFolderController(EmailFolderService emailFolderService) {
        this.emailFolderService = emailFolderService;
    }

    @GetMapping
    @Operation(summary = "获取邮箱文件夹列表")
    public Result<List<EmailFolder>> getFolders() {
        Long userId = UserContext.getUserId();
        emailFolderService.initDefaultFolders(userId);
        List<EmailFolder> folders = emailFolderService.getFoldersByUserId(userId);
        return Result.success(folders);
    }
}
