package com.cloudocs.email.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudocs.email.entity.EmailTag;
import com.cloudocs.email.mapper.EmailTagMapper;
import com.cloudocs.email.service.EmailTagService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmailTagServiceImpl extends ServiceImpl<EmailTagMapper, EmailTag> implements EmailTagService {

    @Override
    public List<EmailTag> getTagsByUserId(Long userId) {
        LambdaQueryWrapper<EmailTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmailTag::getCreatorId, userId);
        return this.list(wrapper);
    }
}
