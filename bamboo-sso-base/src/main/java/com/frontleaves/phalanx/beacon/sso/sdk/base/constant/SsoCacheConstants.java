package com.frontleaves.phalanx.beacon.sso.sdk.base.constant;

/**
 * SSO 缓存相关常量定义
 *
 * @author xiao_lfeng
 * @since 0.0.2
 */
public final class SsoCacheConstants {

    // ==================== 命名 ====================

    public static final String CACHE_MANAGER_NAME = "beaconSsoCacheManager";
    public static final String CACHE_OAUTH_STATE = "oauthState";

    // gRPC 缓存区域
    public static final String CACHE_GRPC_USERINFO = "grpcUserinfo";
    public static final String CACHE_GRPC_MERCHANT_TAG = "grpcMerchantTag";
    public static final String CACHE_GRPC_ANNOUNCEMENT = "grpcAnnouncement";

    // HTTP 缓存区域
    public static final String CACHE_HTTP_USERINFO = "httpUserinfo";
    public static final String CACHE_HTTP_INTROSPECTION = "httpIntrospection";

    // ==================== TTL (秒) ====================

    public static final long CACHE_OAUTH_STATE_TTL = 900L;
    public static final long CACHE_NORMAL_TTL = 10L;

    // ==================== 配置 ====================

    public static final int CACHE_MAXIMUM_SIZE = 10_000;

    private SsoCacheConstants() {
        throw new UnsupportedOperationException("常量类不能被实例化");
    }
}
