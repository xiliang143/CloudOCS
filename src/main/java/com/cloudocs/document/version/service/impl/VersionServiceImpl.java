package com.cloudocs.document.version.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudocs.document.entity.Document;
import com.cloudocs.document.version.entity.DocumentVersion;
import com.cloudocs.document.version.mapper.DocumentVersionMapper;
import com.cloudocs.document.version.service.VersionService;
import com.cloudocs.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VersionServiceImpl extends ServiceImpl<DocumentVersionMapper, DocumentVersion> implements VersionService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void saveVersion(Long documentId, String content, Long creatorId, String remark) {
        // 获取当前最大版本号
        LambdaQueryWrapper<DocumentVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentVersion::getDocumentId, documentId)
               .orderByDesc(DocumentVersion::getVersion)
               .last("LIMIT 1");
        DocumentVersion latest = this.getOne(wrapper);

        int newVersion = 1;
        if (latest != null) {
            newVersion = latest.getVersion() + 1;
        }

        DocumentVersion version = new DocumentVersion();
        version.setDocumentId(documentId);
        version.setContent(content);
        version.setVersion(newVersion);
        version.setCreatorId(creatorId);
        if (UserContext.getUser() != null) {
            version.setCreatorName(UserContext.getUser().getNickname());
        }
        version.setRemark(remark);

        this.save(version);
    }

    @Override
    public List<DocumentVersion> getVersions(Long documentId) {
        LambdaQueryWrapper<DocumentVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentVersion::getDocumentId, documentId)
               .orderByDesc(DocumentVersion::getVersion);
        return this.list(wrapper);
    }

    @Override
    public DocumentVersion getVersionById(Long id) {
        return this.getById(id);
    }

    @Override
    @Transactional
    public void restoreVersion(Long versionId) {
        DocumentVersion version = this.getById(versionId);
        if (version == null) {
            throw new IllegalArgumentException("版本不存在");
        }
        // 发布事件，由DocumentService处理文档恢复
        eventPublisher.publishEvent(new DocumentRestoreEvent(version));
    }

    /**
     * 文档恢复事件
     */
    public static class DocumentRestoreEvent {
        private final DocumentVersion version;

        public DocumentRestoreEvent(DocumentVersion version) {
            this.version = version;
        }

        public DocumentVersion getVersion() {
            return version;
        }
    }
}
