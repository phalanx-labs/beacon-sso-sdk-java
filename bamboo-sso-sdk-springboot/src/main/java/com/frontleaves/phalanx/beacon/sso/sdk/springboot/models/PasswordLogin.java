package com.frontleaves.phalanx.beacon.sso.sdk.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 密码登录响应
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasswordLogin {

    /**
     * Access Token
     */
    private String accessToken;

    /**
     * Token 类型
     */
    private String tokenType;

    /**
     * Access Token 有效期（秒）
     */
    private Long expiresIn;

    /**
     * Refresh Token
     */
    private String refreshToken;

    /**
     * 授权范围
     */
    private String scope;

    /**
     * ID Token
     */
    private String idToken;
}
