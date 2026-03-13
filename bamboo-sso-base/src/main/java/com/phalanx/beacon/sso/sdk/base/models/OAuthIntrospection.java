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
import java.util.List;

/**
 * OAuth 2.0 令牌自省模型
 * <p>
 * 表示令牌自省端点（RFC 7662）的响应结果。
 * 用于验证令牌的有效性并获取令牌的元数据信息。
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
public class OAuthIntrospection implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 令牌激活状态
     * <p>
     * 标识令牌当前是否有效（活跃）。
     * {@code true} 表示令牌有效，{@code false} 表示令牌无效或已过期。
     * </p>
     */
    @JsonProperty("active")
    private boolean active;

    /**
     * 作用域
     * <p>
     * 令牌关联的权限作用域，多个作用域以空格分隔。
     * </p>
     */
    @JsonProperty("scope")
    private String scope;

    /**
     * 客户端 ID
     * <p>
     * 请求该令牌的 OAuth 客户端标识。
     * </p>
     */
    @JsonProperty("client_id")
    private String clientId;

    /**
     * 用户名
     * <p>
     * 与令牌关联的人类可读用户标识符。
     * </p>
     */
    @JsonProperty("username")
    private String username;

    /**
     * 令牌类型
     * <p>
     * 令牌的类型标识符，如 "access_token" 或 "refresh_token"。
     * </p>
     */
    @JsonProperty("token_type")
    private String tokenType;

    /**
     * 过期时间戳
     * <p>
     * 令牌的过期时间（Unix 时间戳，秒）。
     * </p>
     */
    @JsonProperty("exp")
    private Long exp;

    /**
     * 签发时间戳
     * <p>
     * 令牌的签发时间（Unix 时间戳，秒）。
     * </p>
     */
    @JsonProperty("iat")
    private Long iat;

    /**
     * 生效时间戳
     * <p>
     * 令牌开始生效的时间（Unix 时间戳，秒）。
     * 在此时间之前令牌不应被接受。
     * </p>
     */
    @JsonProperty("nbf")
    private Long nbf;

    /**
     * 主体标识
     * <p>
     * 令牌的主体标识，通常是用户唯一标识。
     * </p>
     */
    @JsonProperty("sub")
    private String sub;

    /**
     * 受众列表
     * <p>
     * 令牌的目标受众标识符列表。
     * </p>
     */
    @JsonProperty("aud")
    private List<String> aud;

    /**
     * 签发者
     * <p>
     * 令牌签发者的标识符 URL。
     * </p>
     */
    @JsonProperty("iss")
    private String iss;

    /**
     * JWT ID
     * <p>
     * 令牌的唯一标识符。
     * </p>
     */
    @JsonProperty("jti")
    private String jti;
}
