package com.cloudocs.email.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudocs.email.entity.EmailTag;
import java.util.List;

public interface EmailTagService extends IService<EmailTag> {
    List<EmailTag> getTagsByUserId(Long userId);
}
