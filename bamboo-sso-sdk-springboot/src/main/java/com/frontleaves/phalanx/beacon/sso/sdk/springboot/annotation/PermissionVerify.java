package com.frontleaves.phalanx.beacon.sso.sdk.springboot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限验证注解
 * <p>
 * 用于标注需要进行权限验证的方法或类。
 * 配合 {@link com.frontleaves.phalanx.beacon.sso.sdk.springboot.aspect.PermissionAspect} 使用，
 * 可自动验证用户是否具有所需的权限（基于 scope 字段）。
 * </p>
 *
 * <pre>{@code
 * // 示例：需要单个权限
 * @PermissionVerify("read")
 * public void readData() { ... }
 *
 * // 示例：需要多个权限（全部满足）
 * @PermissionVerify(value = {"read", "write"}, requireAll = true)
 * public void modifyData() { ... }
 *
 * // 示例：需要多个权限（满足其一即可）
 * @PermissionVerify(value = {"admin", "superuser"}, requireAll = false)
 * public void adminOperation() { ... }
 *
 * // 示例：自定义错误消息
 * @PermissionVerify(value = "admin", message = "需要管理员权限")
 * public void adminOnly() { ... }
 * }</pre>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionVerify {

    /**
     * 需要的权限列表
     * <p>
     * 指定访问该方法或类所需的权限标识。
     * 权限验证基于 OAuth 令牌的 scope 字段进行匹配。
     * </p>
     *
     * @return 权限列表，默认为空数组
     */
    String[] value() default {};

    /**
     * 是否需要全部权限
     * <p>
     * 为 {@code true} 时，用户必须拥有所有指定的权限才能访问；
     * 为 {@code false} 时，用户只需拥有其中任意一个权限即可访问。
     * </p>
     *
     * @return 是否需要全部权限，默认为 {@code true}
     */
    boolean requireAll() default true;

    /**
     * 权限不足时的提示消息
     * <p>
     * 当用户权限验证失败时返回的错误消息。
     * </p>
     *
     * @return 提示消息，默认为 "Permission denied"
     */
    String message() default "Permission denied";
}
