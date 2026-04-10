package com.cloudocs.config;

import com.cloudocs.interceptor.HttpTenantInterceptor;
import com.cloudocs.security.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final HttpTenantInterceptor httpTenantInterceptor;
    private final JwtInterceptor jwtInterceptor;

    public WebMvcConfig(HttpTenantInterceptor httpTenantInterceptor, JwtInterceptor jwtInterceptor) {
        this.httpTenantInterceptor = httpTenantInterceptor;
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // JWT拦截器 - 认证所有需要认证的接口
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/document/**", "/api/users/**")
                .excludePathPatterns(
                        "/api/auth/**",
                        "/api/share/**",
                        "/doc.html",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/favicon.ico"
                );

        // 租户拦截器
        registry.addInterceptor(httpTenantInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/doc.html",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/favicon.ico",
                        "/api/auth/**",
                        "/api/share/**"
                );
    }
}
