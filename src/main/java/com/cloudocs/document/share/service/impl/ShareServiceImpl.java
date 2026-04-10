package com.cloudocs.document.share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudocs.document.share.entity.Share;
import com.cloudocs.document.share.mapper.ShareMapper;
import com.cloudocs.document.share.service.ShareService;
import com.cloudocs.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ShareServiceImpl extends ServiceImpl<ShareMapper, Share> implements ShareService {

    @Override
    @Transactional
    public String createShare(Long documentId, Integer accessType, LocalDateTime expireTime) {
        Share share = new Share();
        share.setDocumentId(documentId);
        share.setToken(generateToken());
        share.setAccessType(accessType != null ? accessType : 1);
        share.setExpireTime(expireTime);
        share.setCreatorId(UserContext.getUserId());
        share.setViewCount(0);

        this.save(share);
        return share.getToken();
    }

    @Override
    public Share getShareByToken(String token) {
        LambdaQueryWrapper<Share> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Share::getToken, token);
        return this.getOne(wrapper);
    }

    @Override
    public Share validateShare(String token) {
        Share share = getShareByToken(token);
        if (share == null) {
            throw new IllegalArgumentException("分享不存在");
        }

        if (share.getExpireTime() != null && share.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("分享已过期");
        }

        return share;
    }

    @Override
    @Transactional
    public void incrementViewCount(String token) {
        Share share = getShareByToken(token);
        if (share != null) {
            share.setViewCount(share.getViewCount() == null ? 1 : share.getViewCount() + 1);
            this.updateById(share);
        }
    }

    @Override
    @Transactional
    public void deleteShare(Long id) {
        this.removeById(id);
    }

    @Override
    public List<Share> getSharesByDocumentId(Long documentId) {
        LambdaQueryWrapper<Share> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Share::getDocumentId, documentId)
               .orderByDesc(Share::getCreatedAt);
        return this.list(wrapper);
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
