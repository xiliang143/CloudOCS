package com.cloudocs.tenant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudocs.common.Result;
import com.cloudocs.tenant.entity.Tenant;
import com.cloudocs.tenant.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenants")
@Tag(name = "租户管理")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping
    @Operation(summary = "创建租户")
    public Result<Tenant> save(@RequestBody Tenant tenant) {
        tenantService.saveTenant(tenant);
        return Result.success(tenant);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新租户")
    public Result<Void> update(@PathVariable Long id, @RequestBody Tenant tenant) {
        tenant.setId(id);
        tenantService.updateTenant(tenant);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除租户")
    public Result<Void> delete(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取详情")
    public Result<Tenant> getById(@PathVariable Long id) {
        return Result.success(tenantService.getTenantById(id));
    }

    @GetMapping
    @Operation(summary = "租户列表（分页）")
    public Result<Page<Tenant>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(tenantService.listTenants(page, size));
    }

    @PutMapping("/{id}/audit")
    @Operation(summary = "审核租户")
    public Result<Void> audit(@PathVariable Long id, @RequestParam Integer status) {
        tenantService.auditTenant(id, status);
        return Result.success();
    }

    @PutMapping("/{id}/enable")
    @Operation(summary = "启用租户")
    public Result<Void> enable(@PathVariable Long id) {
        tenantService.enableTenant(id);
        return Result.success();
    }

    @PutMapping("/{id}/disable")
    @Operation(summary = "禁用租户")
    public Result<Void> disable(@PathVariable Long id) {
        tenantService.disableTenant(id);
        return Result.success();
    }
}
