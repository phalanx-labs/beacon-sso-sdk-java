package com.frontleaves.phalanx.beacon.sso.sdk.springboot.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注销 Token 请求
 * <p>
 * 用于注销用户访问令牌或刷新令牌的请求参数。
 * 可通过 {@code tokenTypeHint} 指定要注销的令牌类型。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RevokeTokenRequest {

    /**
     * Token 类型提示（可选）
     * <p>
     * 可选值：
     * <ul>
     *   <li>{@code access_token} - 仅注销 Access Token</li>
     *   <li>{@code refresh_token} - 仅注销 Refresh Token</li>
     *   <li>留空 - 默认注销 Access Token</li>
     * </ul>
     * </p>
     */
    private String tokenTypeHint;
}
