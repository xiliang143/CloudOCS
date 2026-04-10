package com.cloudocs.tenant.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.cloudocs.tenant.entity.Tenant;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 租户管理员Mapper - 不受租户拦截器影响
 */
@Mapper
@InterceptorIgnore(tenantLine = "true")
public interface TenantAdminMapper {

    @Insert("INSERT INTO tenants (name, code, status, package_type, expire_time, created_at, updated_at) " +
            "VALUES (#{name}, #{code}, #{status}, #{packageType}, #{expireTime}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Tenant tenant);

    @Update("<script>" +
            "UPDATE tenants SET updated_at = NOW() " +
            "<if test='name != null'>, name = #{name}</if>" +
            "<if test='code != null'>, code = #{code}</if>" +
            "<if test='status != null'>, status = #{status}</if>" +
            "<if test='packageType != null'>, package_type = #{packageType}</if>" +
            "<if test='expireTime != null'>, expire_time = #{expireTime}</if>" +
            " WHERE id = #{id}" +
            "</script>")
    int update(Tenant tenant);

    @Delete("DELETE FROM tenants WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM tenants WHERE id = #{id}")
    Tenant selectById(Long id);

    @Select("SELECT * FROM tenants WHERE code = #{code}")
    Tenant selectByCode(String code);

    @Select("SELECT * FROM tenants")
    List<Tenant> selectAll();

    @Select("SELECT * FROM tenants LIMIT #{offset}, #{limit}")
    List<Tenant> selectPage(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM tenants")
    long selectCount();

    @Update("UPDATE tenants SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
