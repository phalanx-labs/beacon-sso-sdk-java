package com.phalanx.beacon.sso.sdk.springboot.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phalanx.beacon.sso.sdk.base.models.OAuthIntrospection;
import com.phalanx.beacon.sso.sdk.springboot.annotation.PermissionVerify;
import com.phalanx.beacon.sso.sdk.springboot.filter.BeaconSsoFilter;
import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * 权限验证切面
 * <p>
 * 拦截带有 {@link PermissionVerify} 注解的方法，从请求属性中获取
 * {@link OAuthIntrospection} 对象，验证用户是否具有所需的权限。
 * 权限验证基于 OAuth 令牌的 scope 字段进行匹配。
 * </p>
 *
 * <p>
 * 该切面的执行优先级低于 {@link InjectDataAspect}，确保在权限验证前
 * 数据注入已完成。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@RequiredArgsConstructor
public class PermissionAspect {

    /**
     * JSON 序列化工具
     */
    private final ObjectMapper objectMapper;

    /**
     * 拦截带有 @PermissionVerify 注解的方法（方法级别）
     *
     * @param permissionVerify 权限验证注解
     */
    @Before("@annotation(permissionVerify)")
    public void verifyMethodPermission(PermissionVerify permissionVerify) {
        verifyPermission(permissionVerify);
    }

    /**
     * 拦截带有 @PermissionVerify 注解的类（类级别）
     * <p>
     * 注：方法级别的注解会覆盖类级别的注解
     * </p>
     *
     * @param permissionVerify 权限验证注解
     */
    @Before("@within(permissionVerify) && !@annotation(com.phalanx.beacon.sso.sdk.springboot.annotation.PermissionVerify)")
    public void verifyClassPermission(PermissionVerify permissionVerify) {
        verifyPermission(permissionVerify);
    }

    /**
     * 执行权限验证
     *
     * @param permissionVerify 权限验证注解
     */
    private void verifyPermission(PermissionVerify permissionVerify) {
        String[] requiredPermissions = permissionVerify.value();
        boolean requireAll = permissionVerify.requireAll();
        String message = permissionVerify.message();

        // 如果没有指定权限要求，直接通过
        if (requiredPermissions.length == 0) {
            log.debug("No specific permissions required, allowing access");
            return;
        }

        // 获取请求对象
        HttpServletRequest request = getRequest();
        if (request == null) {
            log.warn("No HTTP request found in context");
            throw new PermissionDeniedException("No request context available");
        }

        // 获取令牌自省信息
        OAuthIntrospection introspection = (OAuthIntrospection) request
                .getAttribute(BeaconSsoFilter.ATTR_INTROSPECTION);

        if (introspection == null) {
            log.warn("No authentication data found in request");
            throw new PermissionDeniedException("Authentication required");
        }

        // 获取用户的权限列表
        Set<String> userPermissions = parseScopes(introspection.getScope());

        log.debug("User permissions: {}", userPermissions);
        log.debug("Required permissions: {}", Arrays.toString(requiredPermissions));

        // 执行权限验证
        boolean hasPermission;
        if (requireAll) {
            // 需要全部权限
            hasPermission = userPermissions.containsAll(Arrays.asList(requiredPermissions));
        } else {
            // 只需要其中一个权限
            hasPermission = Arrays.stream(requiredPermissions)
                    .anyMatch(userPermissions::contains);
        }

        if (!hasPermission) {
            log.warn("Permission denied for user '{}'. Required: {}, Has: {}",
                    introspection.getSub(), Arrays.toString(requiredPermissions), userPermissions);

            // 尝试写入响应（如果可能）
            writeForbiddenResponse(message);

            throw new PermissionDeniedException(message);
        }

        log.debug("Permission verified successfully for user '{}'", introspection.getSub());
    }

    /**
     * 解析 scope 字符串为权限集合
     *
     * @param scope scope 字符串（空格分隔）
     * @return 权限集合
     */
    private Set<String> parseScopes(String scope) {
        if (!StringUtils.hasText(scope)) {
            return new HashSet<>();
        }
        return new HashSet<>(Arrays.asList(scope.split(" ")));
    }

    /**
     * 获取当前 HTTP 请求
     *
     * @return HttpServletRequest 或 null
     */
    private HttpServletRequest getRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest)
                .orElse(null);
    }

    /**
     * 获取当前 HTTP 响应
     *
     * @return HttpServletResponse 或 null
     */
    private HttpServletResponse getResponse() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getResponse)
                .orElse(null);
    }

    /**
     * 写入 403 禁止访问响应
     *
     * @param message 错误消息
     */
    private void writeForbiddenResponse(String message) {
        HttpServletResponse response = getResponse();
        if (response == null) {
            return;
        }

        try {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");

            BaseResponse<Void> errorResponse = new BaseResponse<>(
                    ErrorCode.FORBIDDEN.getOutput(),
                    ErrorCode.FORBIDDEN.getCode(),
                    ErrorCode.FORBIDDEN.getMessage(),
                    message,
                    null
            );

            String responseBody = objectMapper.writeValueAsString(errorResponse);
            response.getWriter().write(responseBody);
            response.getWriter().flush();
        } catch (IOException e) {
            log.error("Failed to write forbidden response", e);
        }
    }

    /**
     * 权限拒绝异常
     * <p>
     * 当用户权限验证失败时抛出此异常。
     * 可配合 Spring 的全局异常处理器使用。
     * </p>
     */
    public static class PermissionDeniedException extends RuntimeException {

        /**
         * 构造函数
         *
         * @param message 错误消息
         */
        public PermissionDeniedException(String message) {
            super(message);
        }
    }
}
