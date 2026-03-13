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
 * OAuth 2.0 Token 响应模型
 * <p>
 * 表示从授权服务器获取的令牌响应，包含访问令牌、刷新令牌等信息。
 * 符合 RFC 6749 OAuth 2.0 标准规范。
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
public class OAuthToken implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 访问令牌
     * <p>
     * 授权服务器颁发的访问令牌，用于访问受保护资源。
     * </p>
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * 令牌类型
     * <p>
     * 令牌的类型标识符，通常为 "Bearer"。
     * 默认值：Bearer
     * </p>
     */
    @JsonProperty("token_type")
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * 过期时间（秒）
     * <p>
     * 访问令牌的有效期，以秒为单位。
     * </p>
     */
    @JsonProperty("expires_in")
    private long expiresIn;

    /**
     * 刷新令牌
     * <p>
     * 用于获取新访问令牌的刷新令牌（可选）。
     * </p>
     */
    @JsonProperty("refresh_token")
    private String refreshToken;

    /**
     * 作用域
     * <p>
     * 访问令牌的权限范围（可选）。
     * </p>
     */
    @JsonProperty("scope")
    private String scope;

    /**
     * 创建时间戳
     * <p>
     * 令牌创建的时间戳（毫秒），用于计算过期时间。
     * </p>
     */
    @JsonProperty("created_at")
    private long createdAt;
}
