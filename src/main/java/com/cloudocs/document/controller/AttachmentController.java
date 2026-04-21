package com.cloudocs.document.controller;

import com.cloudocs.common.Result;
import com.cloudocs.document.entity.Attachment;
import com.cloudocs.document.service.AttachmentService;
import com.cloudocs.security.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/document/attachment")
@Tag(name = "文档附件")
public class AttachmentController {

    private static final Logger log = LoggerFactory.getLogger(AttachmentController.class);

    private final AttachmentService attachmentService;

    private static final String UPLOAD_DIR = "uploads/attachments";
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/upload")
    @Operation(summary = "上传附件到文档")
    public Result<Attachment> uploadAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentId") Long documentId) {

        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return Result.error("文件大小不能超过50MB");
        }

        try {
            // 创建上传目录
            String datePath = new SimpleDateFormat("yyyyMMdd").format(new Date());
            Long userId = UserContext.getUserId();
            if (userId == null) {
                userId = 0L;
            }
            log.info("上传附件 - userId: {}, documentId: {}, filename: {}", userId, documentId, file.getOriginalFilename());

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
            Files.copy(file.getInputStream(), filePath);

            // 创建附件记录
            Attachment attachment = new Attachment();
            attachment.setDocumentId(documentId);
            attachment.setFilename(originalFilename);
            attachment.setUrl("/uploads/attachments/" + subDir + "/" + filename);
            attachment.setSize(file.getSize());
            attachment.setContentType(file.getContentType());
            attachment.setUploaderId(UserContext.getUserId());
            attachment.setUploaderName(UserContext.getUser() != null ? UserContext.getUser().getNickname() : null);

            Attachment result = attachmentService.uploadAttachment(attachment);
            log.info("上传附件成功 - attachmentId: {}", result.getId());
            return Result.success(result);
        } catch (IOException e) {
            log.error("上传附件失败", e);
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    @Operation(summary = "获取文档附件列表")
    public Result<List<Attachment>> listAttachments(@RequestParam Long documentId) {
        return Result.success(attachmentService.getAttachmentsByDocumentId(documentId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除附件")
    public Result<Void> deleteAttachment(@PathVariable Long id) {
        attachmentService.deleteAttachment(id);
        return Result.success();
    }
}
