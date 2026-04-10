package com.cloudocs.security;

import com.cloudocs.tenant.TenantContextHolder;
import com.cloudocs.user.entity.User;
import com.cloudocs.user.service.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final JwtUtils jwtUtils;
    private final UserService userService;

    public JwtInterceptor(JwtUtils jwtUtils, UserService userService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader(AUTH_HEADER);

        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            String token = authHeader.substring(TOKEN_PREFIX.length());

            if (jwtUtils.validateToken(token)) {
                Long userId = jwtUtils.getUserIdFromToken(token);
                Long tenantId = jwtUtils.getTenantIdFromToken(token);
                Integer role = jwtUtils.getRoleFromToken(token);

                // 设置租户上下文
                if (tenantId != null) {
                    TenantContextHolder.setTenantId(tenantId);
                }

                // 从数据库获取完整用户信息并设置到UserContext
                User user = userService.getById(userId);
                if (user != null) {
                    UserContext.setUser(user);
                }

                // 将用户信息存入请求属性
                request.setAttribute("userId", userId);
                request.setAttribute("tenantId", tenantId);
                request.setAttribute("role", role);
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContextHolder.clear();
        UserContext.clear();
    }
}
