package com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 令牌自省请求 DTO
 * <p>
 * 封装令牌自省所需的参数。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntrospectTokenRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -535264115122260975L;

    /**
     * 要自省的令牌
     */
    private String token;

    /**
     * 令牌类型（可选，默认为 "access_token"）
     */
    @Builder.Default
    private String tokenType = "access_token";
}
