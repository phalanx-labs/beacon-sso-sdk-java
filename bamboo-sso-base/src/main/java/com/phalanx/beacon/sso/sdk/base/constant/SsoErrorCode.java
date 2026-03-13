package com.phalanx.beacon.sso.sdk.base.constant;

/**
 * SSO 错误码枚举
 * <p>
 * 定义 OAuth2/OIDC 流程中可能出现的错误类型及对应描述
 * </p>
 *
 * @author xiao_lfeng
 * @since 1.0.0
 */
public enum SsoErrorCode {

    /**
     * 无效或过期的 OAuth state 参数
     */
    INVALID_STATE("invalid_state", "Invalid or expired OAuth state"),

    /**
     * 无效的授权码
     */
    INVALID_CODE("invalid_code", "Invalid authorization code"),

    /**
     * Access Token 已过期
     */
    TOKEN_EXPIRED("token_expired", "Access token has expired"),

    /**
     * Access Token 无效
     */
    TOKEN_INVALID("token_invalid", "Access token is invalid"),

    /**
     * 获取用户信息失败
     */
    USERINFO_FAILED("userinfo_failed", "Failed to fetch user info"),

    /**
     * Token 内省失败
     */
    INTROSPECTION_FAILED("introspection_failed", "Token introspection failed"),

    /**
     * PKCE 生成或验证失败
     */
    PKCE_ERROR("pkce_error", "PKCE generation or verification failed"),

    /**
     * 网络请求失败
     */
    NETWORK_ERROR("network_error", "Network request failed"),

    /**
     * SSO 配置无效
     */
    CONFIGURATION_ERROR("configuration_error", "SSO configuration is invalid");

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误描述
     */
    private final String description;

    SsoErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据错误码获取对应的枚举值
     *
     * @param code 错误码
     * @return 对应的枚举值，若不存在则返回 null
     */
    public static SsoErrorCode fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (SsoErrorCode errorCode : values()) {
            if (errorCode.code.equals(code)) {
                return errorCode;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code + ": " + description;
    }
}
