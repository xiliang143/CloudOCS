package com.cloudocs.tenant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudocs.tenant.entity.Tenant;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {
}
