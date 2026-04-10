package com.cloudocs.tenant;

/**
 * 租户工具类
 */
public class TenantHelper {

    /**
     * 获取当前租户ID
     */
    public static Long getTenantId() {
        return TenantContextHolder.getTenantId();
    }

    /**
     * 获取当前租户ID，如果不存在则抛出异常
     */
    public static Long getRequiredTenantId() {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("租户ID不存在，请检查是否已设置租户上下文");
        }
        return tenantId;
    }

    /**
     * 检查是否存在租户上下文
     */
    public static boolean hasTenant() {
        return TenantContextHolder.hasTenant();
    }

    /**
     * 设置租户ID
     */
    public static void setTenantId(Long tenantId) {
        TenantContextHolder.setTenantId(tenantId);
    }

    /**
     * 清除租户上下文
     */
    public static void clear() {
        TenantContextHolder.clear();
    }
}
