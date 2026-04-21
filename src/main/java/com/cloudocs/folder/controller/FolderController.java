package com.cloudocs.folder.controller;

import com.cloudocs.common.Result;
import com.cloudocs.folder.entity.Folder;
import com.cloudocs.folder.service.FolderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
@Tag(name = "文件夹管理")
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping
    @Operation(summary = "创建文件夹")
    public Result<Folder> createFolder(@RequestBody Folder folder) {
        Folder created = folderService.createFolder(folder);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新文件夹")
    public Result<Void> updateFolder(@PathVariable Long id, @RequestBody Folder folder) {
        folder.setId(id);
        folderService.updateFolder(folder);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文件夹")
    public Result<Void> deleteFolder(@PathVariable Long id) {
        folderService.deleteFolder(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取文件夹详情")
    public Result<Folder> getFolder(@PathVariable Long id) {
        return Result.success(folderService.getFolderById(id));
    }

    @GetMapping("/tree")
    @Operation(summary = "获取文件夹树形结构")
    public Result<List<Folder>> getFolderTree() {
        return Result.success(folderService.getFolderTree());
    }

    @GetMapping("/list")
    @Operation(summary = "获取指定文件夹下的子文件夹")
    public Result<List<Folder>> listFolders(@RequestParam(required = false) Long parentId) {
        if (parentId == null) {
            parentId = 0L;
        }
        return Result.success(folderService.getFoldersByParentId(parentId));
    }
}
