package com.cloudocs.email.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudocs.email.entity.EmailFolder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmailFolderMapper extends BaseMapper<EmailFolder> {
}
