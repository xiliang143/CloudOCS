package com.cloudocs.tenant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudocs.tenant.entity.Tenant;

public interface TenantService {

    /**
     * 创建租户
     */
    void saveTenant(Tenant tenant);

    /**
     * 更新租户
     */
    void updateTenant(Tenant tenant);

    /**
     * 删除租户
     */
    void deleteTenant(Long id);

    /**
     * 根据ID获取
     */
    Tenant getTenantById(Long id);

    /**
     * 根据编码获取
     */
    Tenant getTenantByCode(String code);

    /**
     * 获取所有租户（分页）
     */
    Page<Tenant> listTenants(Integer page, Integer size);

    /**
     * 审核租户
     */
    void auditTenant(Long id, Integer status);

    /**
     * 启用
     */
    void enableTenant(Long id);

    /**
     * 禁用
     */
    void disableTenant(Long id);
}
