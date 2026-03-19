package com.frontleaves.phalanx.beacon.sso.sdk.base.models.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * OAuth State 信息模型
 * <p>
 * 封装 OAuth 授权流程中 State 参数的相关信息，用于防止 CSRF 攻击和 PKCE 验证。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthState implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 状态值（用于防止 CSRF 攻击）
     */
    private String state;

    /**
     * PKCE code_verifier
     */
    private String codeVerifier;

    /**
     * 重定向 URI
     */
    private String redirectUri;

    /**
     * 创建时间戳（毫秒）
     */
    private long createdAt;

    /**
     * 过期时间戳（毫秒）
     */
    private long expiresAt;

    /**
     * 检查 State 是否已过期
     *
     * @return 如果已过期返回 {@code true}，否则返回 {@code false}
     */
    public boolean isExpired() {
        return System.currentTimeMillis() >= expiresAt;
    }
}
