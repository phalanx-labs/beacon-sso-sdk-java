package com.frontleaves.phalanx.beacon.sso.sdk.springboot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据注入注解
 * <p>
 * 用于标注需要从请求属性中自动注入用户数据的方法参数。
 * 配合 {@link com.frontleaves.phalanx.beacon.sso.sdk.springboot.aspect.InjectDataAspect} 使用，
 * 可自动将 {@link com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthIntrospection} 中的
 * 字段值注入到方法参数中。
 * </p>
 *
 * <pre>{@code
 * // 示例：注入用户 ID
 * public void getUserInfo(@InjectData("sub") String userId) { ... }
 *
 * // 示例：注入用户名
 * public void getProfile(@InjectData("username") String username) { ... }
 *
 * // 示例：注入完整的 Introspection 对象
 * public void process(@InjectData OAuthIntrospection introspection) { ... }
 * }</pre>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectData {

    /**
     * 指定要注入的数据字段名
     * <p>
     * 支持的字段包括：
     * <ul>
     *   <li>{@code sub} - 用户唯一标识</li>
     *   <li>{@code username} - 用户名</li>
     *   <li>{@code clientId} - 客户端 ID</li>
     *   <li>{@code scope} - 作用域</li>
     *   <li>{@code tokenType} - 令牌类型</li>
     *   <li>{@code iss} - 签发者</li>
     *   <li>{@code jti} - JWT ID</li>
     * </ul>
     *
     * @return 字段名称，默认为空字符串
     */
    String value() default "";

    /**
     * 是否必须
     * <p>
     * 为 {@code true} 时，如果无法获取到指定字段值，将抛出异常；
     * 为 {@code false} 时，获取失败将注入 {@code null}。
     * </p>
     *
     * @return 是否必须，默认为 {@code true}
     */
    boolean required() default true;
}
