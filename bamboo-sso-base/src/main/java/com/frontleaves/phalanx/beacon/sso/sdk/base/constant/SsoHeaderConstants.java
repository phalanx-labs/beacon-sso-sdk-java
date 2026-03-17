package com.frontleaves.phalanx.beacon.sso.sdk.base.constant;

/**
 * SSO HTTP 请求头常量定义
 * <p>
 * 定义 OAuth2/OIDC 流程中常用的 HTTP 请求头名称和前缀
 * </p>
 *
 * @author xiao_lfeng
 * @since 1.0.0
 */
public final class SsoHeaderConstants {

    /**
     * Authorization 请求头名称
     */
    public static final String AUTHORIZATION = "Authorization";

    /**
     * Bearer Token 前缀（注意包含空格）
     */
    public static final String BEARER_PREFIX = "Bearer ";

    /**
     * Content-Type 请求头名称
     */
    public static final String CONTENT_TYPE = "Content-Type";

    /**
     * Accept 请求头名称
     */
    public static final String ACCEPT = "Accept";

    /**
     * Cache-Control 请求头名称
     */
    public static final String CACHE_CONTROL = "Cache-Control";

    /**
     * 私有构造函数，防止实例化
     */
    private SsoHeaderConstants() {
        throw new UnsupportedOperationException("常量类不能被实例化");
    }
}
