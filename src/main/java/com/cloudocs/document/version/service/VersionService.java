package com.cloudocs.document.version.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudocs.document.version.entity.DocumentVersion;

import java.util.List;

public interface VersionService extends IService<DocumentVersion> {

    /**
     * 保存版本
     */
    void saveVersion(Long documentId, String content, Long creatorId, String remark);

    /**
     * 获取文档所有版本
     */
    List<DocumentVersion> getVersions(Long documentId);

    /**
     * 获取指定版本详情
     */
    DocumentVersion getVersionById(Long id);

    /**
     * 恢复到此版本
     */
    void restoreVersion(Long versionId);
}
