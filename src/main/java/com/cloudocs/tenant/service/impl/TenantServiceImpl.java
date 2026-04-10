package com.cloudocs.tenant.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudocs.tenant.entity.Tenant;
import com.cloudocs.tenant.mapper.TenantAdminMapper;
import com.cloudocs.tenant.mapper.TenantMapper;
import com.cloudocs.tenant.service.TenantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TenantServiceImpl implements TenantService {

    private final TenantMapper tenantMapper;
    private final TenantAdminMapper tenantAdminMapper;

    public TenantServiceImpl(TenantMapper tenantMapper, TenantAdminMapper tenantAdminMapper) {
        this.tenantMapper = tenantMapper;
        this.tenantAdminMapper = tenantAdminMapper;
    }

    @Override
    @Transactional
    public void saveTenant(Tenant tenant) {
        // 检查编码是否已存在
        Tenant existing = tenantAdminMapper.selectByCode(tenant.getCode());
        if (existing != null) {
            throw new IllegalArgumentException("租户编码已存在");
        }
        tenant.setStatus(0);
        tenant.setTenantId(null);
        tenantAdminMapper.insert(tenant);
    }

    @Override
    @Transactional
    public void updateTenant(Tenant tenant) {
        if (tenant.getId() == null) {
            throw new IllegalArgumentException("租户ID不能为空");
        }
        Tenant existing = tenantAdminMapper.selectById(tenant.getId());
        if (existing == null) {
            throw new IllegalArgumentException("租户不存在");
        }
        tenantAdminMapper.update(tenant);
    }

    @Override
    @Transactional
    public void deleteTenant(Long id) {
        tenantAdminMapper.deleteById(id);
    }

    @Override
    public Tenant getTenantById(Long id) {
        return tenantAdminMapper.selectById(id);
    }

    @Override
    public Tenant getTenantByCode(String code) {
        return tenantAdminMapper.selectByCode(code);
    }

    @Override
    public Page<Tenant> listTenants(Integer page, Integer size) {
        long total = tenantAdminMapper.selectCount();
        int offset = (page - 1) * size;
        List<Tenant> records = tenantAdminMapper.selectPage(offset, size);
        Page<Tenant> pager = new Page<>(page, size);
        pager.setTotal(total);
        pager.setRecords(records);
        return pager;
    }

    @Override
    @Transactional
    public void auditTenant(Long id, Integer status) {
        if (status != 1 && status != 2) {
            throw new IllegalArgumentException("审核状态无效");
        }
        Tenant existing = tenantAdminMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("租户不存在");
        }
        tenantAdminMapper.updateStatus(id, status);
    }

    @Override
    @Transactional
    public void enableTenant(Long id) {
        Tenant existing = tenantAdminMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("租户不存在");
        }
        tenantAdminMapper.updateStatus(id, 1);
    }

    @Override
    @Transactional
    public void disableTenant(Long id) {
        Tenant existing = tenantAdminMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("租户不存在");
        }
        tenantAdminMapper.updateStatus(id, 2);
    }
}
