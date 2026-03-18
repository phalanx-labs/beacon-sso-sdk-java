package com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 授权 URL 请求 DTO
 * <p>
 * 封装生成授权 URL 所需的参数，包括 state、code_challenge 和 scope。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationUrlRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -8888221171722749527L;

    /**
     * 状态参数（用于防止 CSRF 攻击）
     */
    private String state;

    /**
     * PKCE code_challenge
     */
    private String codeChallenge;

    /**
     * 作用域（可选，默认为 "openid profile email phone"）
     */
    @Builder.Default
    private String scope = "openid profile email phone";
}
