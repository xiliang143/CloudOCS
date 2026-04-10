package com.cloudocs.document.controller;

import com.cloudocs.common.Result;
import com.cloudocs.document.entity.Document;
import com.cloudocs.document.service.DocumentService;
import com.cloudocs.document.share.service.ShareService;
import com.cloudocs.document.version.entity.DocumentVersion;
import com.cloudocs.document.version.service.VersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/document")
@Tag(name = "文档管理")
public class DocumentController {

    private final DocumentService documentService;
    private final VersionService versionService;
    private final ShareService shareService;

    public DocumentController(DocumentService documentService, VersionService versionService, ShareService shareService) {
        this.documentService = documentService;
        this.versionService = versionService;
        this.shareService = shareService;
    }

    @PostMapping
    @Operation(summary = "创建文档")
    public Result<Void> createDocument(@RequestBody Document document) {
        documentService.createDocument(document);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新文档")
    public Result<Void> updateDocument(@PathVariable Long id, @RequestBody Document document) {
        document.setId(id);
        documentService.updateDocument(document);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "软删除")
    public Result<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return Result.success();
    }

    @DeleteMapping("/{id}/permanent")
    @Operation(summary = "彻底删除")
    public Result<Void> permanentDelete(@PathVariable Long id) {
        documentService.permanentDelete(id);
        return Result.success();
    }

    @PostMapping("/{id}/restore")
    @Operation(summary = "恢复")
    public Result<Void> restoreDocument(@PathVariable Long id) {
        documentService.restoreDocument(id);
        return Result.success();
    }

    @PutMapping("/{id}/move")
    @Operation(summary = "移动")
    public Result<Void> moveDocument(@PathVariable Long id, @RequestParam Long targetFolderId) {
        documentService.moveDocument(id, targetFolderId);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取详情")
    public Result<Document> getDocument(@PathVariable Long id) {
        return Result.success(documentService.getDocumentById(id));
    }

    @GetMapping("/list")
    @Operation(summary = "文档列表")
    public Result<List<Document>> listDocuments(@RequestParam(required = false) Long parentId) {
        if (parentId == null) {
            parentId = 0L;
        }
        return Result.success(documentService.getDocumentsByFolderId(parentId));
    }

    @GetMapping("/tree")
    @Operation(summary = "文档树")
    public Result<List<Document>> getDocumentTree() {
        return Result.success(documentService.getDocumentTree());
    }

    @GetMapping("/deleted")
    @Operation(summary = "回收站列表")
    public Result<List<Document>> getDeletedDocuments() {
        return Result.success(documentService.getDeletedDocuments());
    }

    // ========== 版本管理 ==========

    @PostMapping("/{id}/versions")
    @Operation(summary = "保存版本")
    public Result<Void> saveVersion(@PathVariable Long id,
                                   @RequestParam(required = false) String content,
                                   @RequestParam(required = false) String remark) {
        versionService.saveVersion(id, content, com.cloudocs.security.UserContext.getUserId(), remark);
        return Result.success();
    }

    @GetMapping("/{id}/versions")
    @Operation(summary = "获取版本列表")
    public Result<List<DocumentVersion>> getVersions(@PathVariable Long id) {
        return Result.success(versionService.getVersions(id));
    }

    @GetMapping("/version/{versionId}")
    @Operation(summary = "获取指定版本")
    public Result<DocumentVersion> getVersion(@PathVariable Long versionId) {
        return Result.success(versionService.getVersionById(versionId));
    }

    @PostMapping("/version/{versionId}/restore")
    @Operation(summary = "恢复版本")
    public Result<Void> restoreVersion(@PathVariable Long versionId) {
        versionService.restoreVersion(versionId);
        return Result.success();
    }

    // ========== 分享管理 ==========

    @PostMapping("/{id}/share")
    @Operation(summary = "创建分享")
    public Result<String> createShare(@PathVariable Long id,
                                    @RequestParam(defaultValue = "1") Integer accessType) {
        String token = shareService.createShare(id, accessType, null);
        return Result.success(token);
    }

    @GetMapping("/share/list")
    @Operation(summary = "获取文档的所有分享")
    public Result<List<com.cloudocs.document.share.entity.Share>> getShares(@RequestParam Long documentId) {
        return Result.success(shareService.getSharesByDocumentId(documentId));
    }
}
