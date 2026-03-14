package com.frontleaves.phalanx.beacon.sso.sdk.base.constant;

/**
 * SSO 缓存相关常量定义
 * <p>
 * 定义缓存管理器名称、缓存区域名称及默认 TTL 配置
 * </p>
 *
 * @author xiao_lfeng
 * @since 1.0.0
 */
public final class SsoCacheConstants {

    /**
     * SSO 缓存管理器名称
     */
    public static final String CACHE_MANAGER_NAME = "beaconSsoCacheManager";

    /**
     * OAuth State 缓存区域名称
     * <p>
     * 用于存储 OAuth2 授权流程中的 state 参数，防止 CSRF 攻击
     * </p>
     */
    public static final String CACHE_OAUTH_STATE = "oauthState";

    /**
     * OAuth State 缓存默认 TTL（秒）
     * <p>
     * 15 分钟 = 900 秒
     * </p>
     */
    public static final long CACHE_OAUTH_STATE_TTL = 900L;

    /**
     * OAuth Token 缓存区域名称
     * <p>
     * 用于存储访问令牌和刷新令牌，TTL 根据 token 的 expires_in 动态设置
     * </p>
     */
    public static final String CACHE_OAUTH_TOKEN = "oauthToken";

    /**
     * 用户信息缓存区域名称
     * <p>
     * 用于存储从 UserInfo Endpoint 获取的用户信息
     * </p>
     */
    public static final String CACHE_USERINFO = "userinfo";

    /**
     * 用户信息缓存默认 TTL（秒）
     * <p>
     * 60 秒
     * </p>
     */
    public static final long CACHE_USERINFO_TTL = 60L;

    /**
     * Token 内省缓存区域名称
     * <p>
     * 用于存储 token introspection 结果
     * </p>
     */
    public static final String CACHE_INTROSPECTION = "introspection";

    /**
     * Token 内省缓存默认 TTL（秒）
     * <p>
     * 60 秒
     * </p>
     */
    public static final long CACHE_INTROSPECTION_TTL = 60L;

    /**
     * 私有构造函数，防止实例化
     */
    private SsoCacheConstants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}
