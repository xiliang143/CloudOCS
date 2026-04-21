package com.cloudocs.email.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudocs.email.entity.EmailFolder;
import com.cloudocs.email.mapper.EmailFolderMapper;
import com.cloudocs.email.service.EmailFolderService;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class EmailFolderServiceImpl extends ServiceImpl<EmailFolderMapper, EmailFolder> implements EmailFolderService {

    @Override
    public List<EmailFolder> getFoldersByUserId(Long userId) {
        LambdaQueryWrapper<EmailFolder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmailFolder::getUserId, userId).orderByAsc(EmailFolder::getSortOrder);
        return this.list(wrapper);
    }

    @Override
    public void initDefaultFolders(Long userId) {
        LambdaQueryWrapper<EmailFolder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmailFolder::getUserId, userId);
        long count = this.count(wrapper);
        if (count > 0) {
            return;
        }

        List<EmailFolder> defaultFolders = Arrays.asList(
            createFolder(userId, "INBOX", "收件箱", "MessageBox", 1),
            createFolder(userId, "SENT", "已发送", "Promotion", 2),
            createFolder(userId, "DRAFT", "草稿箱", "Document", 3),
            createFolder(userId, "ARCHIVE", "归档", "FolderOpened", 4),
            createFolder(userId, "SPAM", "垃圾箱", "WarnTriangleFilled", 5),
            createFolder(userId, "TRASH", "已删除", "Delete", 6)
        );
        this.saveBatch(defaultFolders);
    }

    private EmailFolder createFolder(Long userId, String type, String name, String icon, int sortOrder) {
        EmailFolder folder = new EmailFolder();
        folder.setUserId(userId);
        folder.setType(type);
        folder.setName(name);
        folder.setIcon(icon);
        folder.setSortOrder(sortOrder);
        folder.setUnreadCount(0);
        return folder;
    }
}
