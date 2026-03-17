package com.frontleaves.phalanx.beacon.sso.sdk.base.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 密码登录响应 DTO
 * <p>
 * 对应 gRPC PasswordLoginResponse，封装登录成功后的令牌信息。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoLoginResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Access Token
     */
    private String accessToken;

    /**
     * Token 类型（"Bearer"）
     */
    private String tokenType;

    /**
     * 有效期（秒）
     */
    private long expiresIn;

    /**
     * Refresh Token
     */
    private String refreshToken;

    /**
     * 授权范围
     */
    private String scope;

    /**
     * ID Token（JWT）
     */
    private String idToken;
}
