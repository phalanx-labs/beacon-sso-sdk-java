package com.phalanx.beacon.sso.sdk.base.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * OAuth 2.0 State 存储模型
 * <p>
 * 用于存储 OAuth 授权流程中的状态信息，支持 PKCE（Proof Key for Code Exchange）扩展。
 * 该模型用于在授权码流程中维护状态，防止 CSRF 攻击。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1-SNAPSHOT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthState implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 状态值
     * <p>
     * 用于防止 CSRF 攻击的随机状态字符串。
     * 在授权请求和回调中必须保持一致。
     * </p>
     */
    @JsonProperty("state")
    private String state;

    /**
     * PKCE 代码验证器
     * <p>
     * 用于 PKCE 扩展的代码验证器（Code Verifier）。
     * 是一个高熵加密随机字符串，用于在令牌请求时验证授权码。
     * </p>
     */
    @JsonProperty("code_verifier")
    private String codeVerifier;

    /**
     * 重定向 URI
     * <p>
     * 授权服务器在完成授权后重定向的 URI。
     * 必须与授权请求中指定的 redirect_uri 完全匹配。
     * </p>
     */
    @JsonProperty("redirect_uri")
    private String redirectUri;

    /**
     * 创建时间戳
     * <p>
     * 状态信息创建的时间戳（毫秒）。
     * </p>
     */
    @JsonProperty("created_at")
    private long createdAt;

    /**
     * 过期时间戳
     * <p>
     * 状态信息过期的时间戳（毫秒）。
     * 过期后该状态应被视为无效。
     * </p>
     */
    @JsonProperty("expires_at")
    private long expiresAt;

    /**
     * 检查状态是否已过期
     *
     * @return 如果当前时间已超过过期时间，返回 {@code true}
     */
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
}
