package com.cloudocs.document.share.controller;

import com.cloudocs.common.Result;
import com.cloudocs.document.share.entity.Share;
import com.cloudocs.document.share.service.ShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/document/share")
@Tag(name = "文档分享管理")
public class ShareController {

    private final ShareService shareService;

    public ShareController(ShareService shareService) {
        this.shareService = shareService;
    }

    @PostMapping
    @Operation(summary = "生成分享链接")
    public Result<String> createShare(@RequestParam Long documentId,
                                      @RequestParam(defaultValue = "1") Integer accessType,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime expireTime) {
        String token = shareService.createShare(documentId, accessType, expireTime);
        return Result.success(token);
    }

    @GetMapping("/validate")
    @Operation(summary = "验证分享token")
    public Result<Share> validateToken(@RequestParam String token) {
        Share share = shareService.validateShare(token);
        return Result.success(share);
    }

    @GetMapping("/{token}")
    @Operation(summary = "获取分享信息")
    public Result<Share> getByToken(@PathVariable String token) {
        return Result.success(shareService.getShareByToken(token));
    }

    @PostMapping("/{id}/disable")
    @Operation(summary = "禁用分享")
    public Result<Void> disableShare(@PathVariable Long id) {
        shareService.removeById(id);
        return Result.success();
    }

    @GetMapping("/shares")
    @Operation(summary = "获取文档的所有分享链接")
    public Result<List<Share>> getShares(@RequestParam Long documentId) {
        return Result.success(shareService.getSharesByDocumentId(documentId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除分享链接")
    public Result<Void> deleteShare(@PathVariable Long id) {
        shareService.deleteShare(id);
        return Result.success();
    }
}
