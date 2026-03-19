package com.frontleaves.phalanx.beacon.sso.sdk.springboot.aspect;

import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.oauth.IntrospectResult;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.annotation.InjectData;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.filter.BeaconSsoFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Optional;

/**
 * 数据注入切面
 * <p>
 * 处理带有 {@link InjectData} 注解的方法参数，从请求属性中获取
 * {@link IntrospectResult} 对象，并根据注解配置注入相应的字段值。
 * </p>
 *
 * <p>
 * 该切面的执行优先级高于 {@link PermissionAspect}，确保在权限验证前
 * 数据已经准备就绪。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class InjectDataAspect {

    /**
     * 拦截所有 Controller 方法，处理带有 @InjectData 注解的参数
     *
     * @param joinPoint 切点
     * @return 方法执行结果
     * @throws Throwable 方法执行异常
     */
    @Around("@within(org.springframework.web.bind.annotation.RestController) || " +
            "@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public Object injectData(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        // 获取请求对象
        HttpServletRequest request = getRequest();
        if (request == null) {
            log.debug("No HTTP request found in context, skipping data injection");
            return joinPoint.proceed();
        }

        // 获取令牌自省信息
        IntrospectResult introspection = (IntrospectResult) request
                .getAttribute(BeaconSsoFilter.ATTR_INTROSPECTION);

        if (introspection == null) {
            log.debug("No IntrospectResult found in request attributes");
        }

        // 遍历参数，处理带有 @InjectData 注解的参数
        for (int i = 0; i < parameters.length; i++) {
            InjectData annotation = parameters[i].getAnnotation(InjectData.class);
            if (annotation == null) {
                continue;
            }

            // 注入数据
            args[i] = resolveInjectValue(annotation, parameters[i].getType(), introspection);
        }

        return joinPoint.proceed(args);
    }

    /**
     * 解析注入值
     *
     * @param annotation    注解实例
     * @param parameterType 参数类型
     * @param introspection 令牌自省信息
     * @return 注入值
     */
    private Object resolveInjectValue(
            InjectData annotation,
            Class<?> parameterType,
            IntrospectResult introspection
    ) {
        // 如果没有自省信息
        if (introspection == null) {
            if (annotation.required()) {
                throw new IllegalStateException("请求中未找到认证数据");
            }
            return null;
        }

        String fieldName = annotation.value();

        // 如果未指定字段名，注入整个对象
        if (!StringUtils.hasText(fieldName)) {
            if (parameterType.isAssignableFrom(IntrospectResult.class)) {
                return introspection;
            }
            if (annotation.required()) {
                throw new IllegalArgumentException(
                        "未指定字段时参数类型必须为 IntrospectResult");
            }
            return null;
        }

        // 根据字段名注入对应值
        Object value = extractFieldValue(fieldName, introspection);

        if (value == null && annotation.required()) {
            throw new IllegalArgumentException(
                    "在认证数据中未找到必要字段 '" + fieldName + "'");
        }

        // 类型转换检查
        if (value != null && !parameterType.isAssignableFrom(value.getClass())) {
            // 尝试进行简单类型转换
            value = convertValue(value, parameterType);
        }

        return value;
    }

    /**
     * 从 IntrospectResult 中提取字段值
     *
     * @param fieldName     字段名
     * @param introspection 令牌自省信息
     * @return 字段值
     */
    private Object extractFieldValue(String fieldName, IntrospectResult introspection) {
        return switch (fieldName) {
            case "sub" -> introspection.getSub();
            case "username" -> introspection.getUsername();
            case "clientId" -> introspection.getClientId();
            case "scope" -> introspection.getScope();
            case "tokenType" -> introspection.getTokenType();
            case "iss" -> introspection.getIss();
            case "jti" -> introspection.getJti();
            case "exp" -> introspection.getExp();
            case "iat" -> introspection.getIat();
            case "nbf" -> introspection.getNbf();
            case "aud" -> introspection.getAud();
            case "active" -> introspection.getActive();
            default -> {
                log.warn("未知的注入字段名 '{}'", fieldName);
                yield null;
            }
        };
    }

    /**
     * 尝试进行值类型转换
     *
     * @param value      原始值
     * @param targetType 目标类型
     * @return 转换后的值
     */
    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        // String 类型转换
        if (targetType == String.class) {
            return value.toString();
        }

        // Boolean 类型转换
        if (targetType == boolean.class || targetType == Boolean.class) {
            if (value instanceof Boolean) {
                return value;
            }
            return Boolean.parseBoolean(value.toString());
        }

        // Long 类型转换
        if (targetType == long.class || targetType == Long.class) {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            try {
                return Long.parseLong(value.toString());
            } catch (NumberFormatException e) {
                log.warn("无法将 '{}' 转换为 Long", value);
                return null;
            }
        }

        // List 类型
        if (targetType.isAssignableFrom(java.util.List.class)) {
            if (value instanceof java.util.List) {
                return value;
            }
            // 尝试将字符串按空格分割为列表（scope 字段）
            if (value instanceof String str) {
                return Arrays.asList(str.split(" "));
            }
        }

        log.warn("无法将 '{}' 转换为目标类型 '{}'", value, targetType.getName());
        return value;
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
}
