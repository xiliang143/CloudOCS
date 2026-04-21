package com.cloudocs.document.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudocs.document.entity.Document;

import java.util.List;

public interface DocumentService extends IService<Document> {

    /**
     * 创建文档
     */
    Document createDocument(Document document);

    /**
     * 更新文档
     */
    void updateDocument(Document document);

    /**
     * 软删除
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
     * 获取指定文件夹下的文档列表
     */
    List<Document> getDocumentsByFolderId(Long folderId);

    /**
     * 移动文档到目标文件夹
     */
    void moveDocument(Long id, Long targetFolderId);

    /**
     * 获取回收站文档列表
     */
    List<Document> getDeletedDocuments();

    /**
     * 清空回收站
     */
    void emptyTrash();

    // ========== 邮箱功能 ==========

    /**
     * 按类型获取邮件列表
     */
    List<Document> getEmailsByType(String type);

    /**
     * 设置优先级
     */
    void setPriority(Long id, Integer priority);

    /**
     * 设置星标
     */
    void setStar(Long id, Integer starred);

    /**
     * 设置标签
     */
    void setTags(Long id, String tags);

    /**
     * 标记已读/未读
     */
    void markRead(Long id, Integer isRead);

    /**
     * 发送邮件
     */
    void sendEmail(Long id);

    /**
     * 获取未读邮件数
     */
    int getUnreadCount();

    /**
     * 创建并发送邮件
     */
    Document createEmail(Document document);
}
