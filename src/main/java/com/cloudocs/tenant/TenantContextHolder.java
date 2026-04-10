package com.cloudocs.tenant;

/**
 * 租户上下文持有器，使用 ThreadLocal 存储当前线程的租户ID
 */
public class TenantContextHolder {

    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();

    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static Long getTenantId() {
        return TENANT_ID.get();
    }

    public static void clear() {
        TENANT_ID.remove();
    }

    public static boolean hasTenant() {
        return TENANT_ID.get() != null;
    }
}
