package com.cloudocs.document.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudocs.document.entity.Document;

import java.util.List;

public interface DocumentService extends IService<Document> {

    /**
     * 创建文档/文件夹
     */
    void createDocument(Document document);

    /**
     * 更新文档
     */
    void updateDocument(Document document);

    /**
     * 软删除（isDeleted=1）
     */
    void deleteDocument(Long id);

    /**
     * 彻底删除
     */
    void permanentDelete(Long id);

    /**
     * 恢复已删除文档
     */
    void restoreDocument(Long id);

    /**
     * 获取详情
     */
    Document getDocumentById(Long id);

    /**
     * 获取文件夹下的文档列表
     */
    List<Document> getDocumentsByFolderId(Long parentId);

    /**
     * 获取文档树形结构
     */
    List<Document> getDocumentTree();

    /**
     * 移动文档到目标文件夹
     */
    void moveDocument(Long id, Long targetFolderId);

    /**
     * 获取回收站文档列表
     */
    List<Document> getDeletedDocuments();
}
