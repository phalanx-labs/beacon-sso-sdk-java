package com.frontleaves.phalanx.beacon.sso.sdk.base.constant;

/**
 * SSO gRPC 元数据常量定义
 * <p>
 * 用于在 gRPC 调用中携带 App 凭证与用户认证信息。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
public final class SsoGrpcConstants {

    /**
     * App Access ID 元数据键
     */
    public static final String APP_ACCESS_ID_HEADER = "app-access-id";

    /**
     * App Secret Key 元数据键
     */
    public static final String APP_SECRET_KEY_HEADER = "app-secret-key";

    /**
     * Authorization 元数据键
     */
    public static final String AUTHORIZATION_HEADER = "authorization";

    /**
     * Bearer Token 前缀（注意包含空格）
     */
    public static final String BEARER_PREFIX = "Bearer ";

    private SsoGrpcConstants() {
        throw new UnsupportedOperationException("常量类不能被实例化");
    }
}
