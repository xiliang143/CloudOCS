package com.cloudocs.aspect;

import com.cloudocs.security.UserContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * Controller 日志切面
 * 记录每个接口的调用日志
 */
@Aspect
@Component
public class ControllerLogAspect {

    private static final Logger log = LoggerFactory.getLogger(ControllerLogAspect.class);

    /**
     * 定义切点：所有 Controller 类的所有方法
     */
    @Pointcut("execution(* com.cloudocs..controller..*.*(..))")
    public void controllerPointcut() {
    }

    /**
     * 环绕通知：记录接口调用日志
     */
    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        String requestMethod = request != null ? request.getMethod() : "UNKNOWN";
        String requestUri = request != null ? request.getRequestURI() : "";

        // 获取用户信息
        Long userId = UserContext.getUserId();
        Long tenantId = null;
        try {
            tenantId = com.cloudocs.tenant.TenantContextHolder.getTenantId();
        } catch (Exception ignored) {
        }

        // 获取参数
        Object[] args = joinPoint.getArgs();
        String params = getParams(args);

        // 打印请求日志
        log.info("=== API Call ===");
        log.info("URL: {} {}", requestMethod, requestUri);
        log.info("Controller: {}.{}", className, methodName);
        log.info("User: {}, Tenant: {}", userId, tenantId);
        log.info("Params: {}", params);

        Object result = null;
        Throwable error = null;

        try {
            // 执行目标方法
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            long costTime = System.currentTimeMillis() - startTime;

            if (error != null) {
                log.error("=== API Error ===");
                log.error("URL: {} {}", requestMethod, requestUri);
                log.error("Controller: {}.{}", className, methodName);
                log.error("Cost: {}ms, Error: {}", costTime, error.getMessage());
            } else {
                log.info("=== API Success ===");
                log.info("URL: {} {}", requestMethod, requestUri);
                log.info("Controller: {}.{}", className, methodName);
                log.info("Cost: {}ms", costTime);
            }
            log.info("================");
        }
    }

    /**
     * 获取方法参数
     */
    private String getParams(Object[] args) {
        if (args == null || args.length == 0) {
            return "none";
        }
        return Arrays.stream(args)
                .filter(arg -> arg != null)
                .filter(arg -> !(arg instanceof HttpServletRequest))
                .filter(arg -> !(arg instanceof org.springframework.web.multipart.MultipartFile))
                .map(arg -> {
                    if (arg instanceof String || arg instanceof Number || arg instanceof Boolean) {
                        return arg.toString();
                    }
                    // 对于复杂对象，只显示类名
                    return arg.getClass().getSimpleName();
                })
                .reduce((a, b) -> a + ", " + b)
                .orElse("none");
    }
}
