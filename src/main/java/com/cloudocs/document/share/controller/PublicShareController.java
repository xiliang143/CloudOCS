package com.cloudocs.document.share.controller;

import com.cloudocs.common.Result;
import com.cloudocs.document.entity.Document;
import com.cloudocs.document.share.entity.Share;
import com.cloudocs.document.share.service.ShareService;
import com.cloudocs.document.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/share")
@Tag(name = "文档分享")
public class PublicShareController {

    private final ShareService shareService;
    private final DocumentService documentService;

    public PublicShareController(ShareService shareService, DocumentService documentService) {
        this.shareService = shareService;
        this.documentService = documentService;
    }

    @GetMapping("/{token}")
    @Operation(summary = "通过token访问分享（无需登录）")
    public Result<Document> getSharedDocument(@PathVariable String token) {
        Share share = shareService.validateShare(token);
        shareService.incrementViewCount(token);

        Document document = documentService.getDocumentById(share.getDocumentId());
        return Result.success(document);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除分享")
    public Result<Void> deleteShare(@PathVariable Long id) {
        shareService.deleteShare(id);
        return Result.success();
    }
}
