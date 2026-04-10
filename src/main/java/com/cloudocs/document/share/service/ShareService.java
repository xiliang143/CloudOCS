package com.cloudocs.document.share.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudocs.document.share.entity.Share;

import java.time.LocalDateTime;
import java.util.List;

public interface ShareService extends IService<Share> {

    /**
     * 创建分享
     */
    String createShare(Long documentId, Integer accessType, LocalDateTime expireTime);

    /**
     * 通过token获取分享信息
     */
    Share getShareByToken(String token);

    /**
     * 验证分享是否有效
     */
    Share validateShare(String token);

    /**
     * 浏览次数+1
     */
    void incrementViewCount(String token);

    /**
     * 删除分享
     */
    void deleteShare(Long id);

    /**
     * 获取文档的所有分享
     */
    List<Share> getSharesByDocumentId(Long documentId);
}
