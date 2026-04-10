package com.cloudocs.interceptor;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.cloudocs.tenant.TenantContextHolder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.stereotype.Component;

/**
 * MyBatis Plus 租户拦截器
 * 实现 TenantLineHandler 接口
 * 从 TenantContextHolder 获取 tenant_id
 */
@Component
public class TenantInterceptor implements TenantLineHandler {

    @Override
    public Expression getTenantId() {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null) {
            return null;
        }
        return new LongValue(tenantId);
    }

    @Override
    public String getTenantIdColumn() {
        return "tenant_id";
    }
}
