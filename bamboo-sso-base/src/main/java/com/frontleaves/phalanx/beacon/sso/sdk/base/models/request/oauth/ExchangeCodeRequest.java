package com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 授权码交换请求 DTO
 * <p>
 * 封装使用授权码交换令牌所需的参数。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeCodeRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 授权码
     */
    private String code;

    /**
     * 重定向 URI
     */
    private String redirectUri;

    /**
     * PKCE code_verifier
     */
    private String codeVerifier;
}
