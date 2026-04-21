package com.cloudocs.common.controller;

import com.cloudocs.common.Result;
import com.cloudocs.common.UploadResult;
import com.cloudocs.security.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@Tag(name = "文件上传")
public class UploadController {

    private static final String UPLOAD_DIR = "uploads";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @PostMapping
    @Operation(summary = "上传文件")
    public Result<UploadResult> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return Result.error("文件大小不能超过10MB");
        }

        try {
            // 创建上传目录
            String datePath = new SimpleDateFormat("yyyyMMdd").format(new Date());
            Long userId = UserContext.getUserId();
            if (userId == null) {
                userId = 0L;
            }

            String subDir = userId + "/" + datePath;
            Path uploadPath = Paths.get(UPLOAD_DIR, subDir);
            Files.createDirectories(uploadPath);

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = uploadPath.resolve(filename);
            file.transferTo(filePath.toFile());

            // 返回访问URL
            String url = "/uploads/" + subDir + "/" + filename;

            UploadResult result = new UploadResult();
            result.setUrl(url);
            result.setFilename(originalFilename);
            result.setSize(file.getSize());

            return Result.success(result);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
