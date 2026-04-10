package com.cloudocs.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudocs.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
