package com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 令牌自省结果 DTO
 * <p>
 * 封装令牌自省后返回的结果信息。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntrospectResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 令牌是否有效
     */
    private Boolean active;

    /**
     * 作用域
     */
    private String scope;

    /**
     * 客户端 ID
     */
    private String clientId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 令牌类型
     */
    private String tokenType;

    /**
     * 过期时间（Unix 时间戳）
     */
    private Long exp;

    /**
     * 签发时间（Unix 时间戳）
     */
    private Long iat;

    /**
     * 生效时间（Unix 时间戳）
     */
    private Long nbf;

    /**
     * 主体标识
     */
    private String sub;

    /**
     * 受众
     */
    private String aud;

    /**
     * 签发者
     */
    private String iss;

    /**
     * 令牌唯一标识
     */
    private String jti;
}
