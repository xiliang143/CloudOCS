package com.cloudocs.email.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudocs.email.entity.EmailFolder;
import java.util.List;

public interface EmailFolderService extends IService<EmailFolder> {
    List<EmailFolder> getFoldersByUserId(Long userId);
    void initDefaultFolders(Long userId);
}
