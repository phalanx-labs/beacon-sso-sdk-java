package com.frontleaves.phalanx.beacon.sso.sdk.springboot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OAuth 回调响应 DTO
 * <p>
 * 用于封装 OAuth 2.0 授权码回调成功后的响应数据。
 * </p>
 *
 * @param tokenType    令牌类型，通常为 "Bearer"
 * @param expiresIn    过期时间（秒）
 * @param scope        授权范围
 * @param refreshToken 刷新令牌（可选）
 * @author xiao_lfeng
 * @since 0.0.1-SNAPSHOT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OAuthCallbackResponseDTO {

    /**
     * 令牌类型，通常为 "Bearer"
     */
    private String tokenType;

    /**
     * 过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 授权范围
     */
    private String scope;

    /**
     * 刷新令牌（可选）
     */
    private String refreshToken;
}
