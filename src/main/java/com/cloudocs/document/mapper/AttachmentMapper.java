package com.cloudocs.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudocs.document.entity.Attachment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AttachmentMapper extends BaseMapper<Attachment> {
}
