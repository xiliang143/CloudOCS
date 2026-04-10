package com.cloudocs.interceptor;

import com.cloudocs.tenant.TenantContextHolder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * HTTP请求租户拦截器
 * 从请求头或参数获取 tenant_id 并设置到上下文
 */
@Component
public class HttpTenantInterceptor implements HandlerInterceptor {

    private static final String TENANT_HEADER = "X-Tenant-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantIdStr = request.getHeader(TENANT_HEADER);
        if (tenantIdStr == null || tenantIdStr.isEmpty()) {
            tenantIdStr = request.getParameter("tenant_id");
        }
        if (tenantIdStr != null && !tenantIdStr.isEmpty()) {
            try {
                Long tenantId = Long.parseLong(tenantIdStr);
                TenantContextHolder.setTenantId(tenantId);
            } catch (NumberFormatException e) {
                // 忽略无效格式
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContextHolder.clear();
    }
}
