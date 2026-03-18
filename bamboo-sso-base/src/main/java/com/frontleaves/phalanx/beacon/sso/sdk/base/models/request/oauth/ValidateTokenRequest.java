package com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 令牌验证请求 DTO
 * <p>
 * 封装令牌验证所需的参数。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateTokenRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 要验证的令牌
     */
    private String token;

    /**
     * 令牌类型（可选，默认为 "access_token"）
     */
    @Builder.Default
    private String tokenType = "access_token";
}
