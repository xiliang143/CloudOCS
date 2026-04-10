package com.cloudocs.document.version.controller;

import com.cloudocs.common.Result;
import com.cloudocs.document.version.entity.DocumentVersion;
import com.cloudocs.document.version.service.VersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/document/version")
@Tag(name = "文档版本管理")
public class VersionController {

    private final VersionService versionService;

    public VersionController(VersionService versionService) {
        this.versionService = versionService;
    }

    @PostMapping
    @Operation(summary = "保存版本快照")
    public Result<Void> saveVersion(@RequestParam Long documentId,
                                    @RequestParam(required = false) String content,
                                    @RequestParam Long creatorId,
                                    @RequestParam(required = false) String description) {
        versionService.saveVersion(documentId, content, creatorId, description);
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(summary = "获取文档版本列表")
    public Result<List<DocumentVersion>> getVersions(@RequestParam Long documentId) {
        return Result.success(versionService.getVersions(documentId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取指定版本")
    public Result<DocumentVersion> getVersion(@PathVariable Long id) {
        return Result.success(versionService.getVersionById(id));
    }

    @GetMapping("/latest")
    @Operation(summary = "获取最新版本")
    public Result<DocumentVersion> getLatestVersion(@RequestParam Long documentId) {
        List<DocumentVersion> versions = versionService.getVersions(documentId);
        if (versions != null && !versions.isEmpty()) {
            return Result.success(versions.get(0));
        }
        return Result.success(null);
    }

    @PostMapping("/{id}/restore")
    @Operation(summary = "恢复指定版本")
    public Result<Void> restoreVersion(@PathVariable Long id) {
        versionService.restoreVersion(id);
        return Result.success();
    }
}
