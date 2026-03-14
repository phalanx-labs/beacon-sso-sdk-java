package com.frontleaves.phalanx.beacon.sso.sdk.springboot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OAuth 登出响应 DTO
 * <p>
 * 用于封装 OAuth 登出操作的响应结果。
 * </p>
 *
 * @param tokenRevoked 令牌是否成功撤销
 * @author xiao_lfeng
 * @since 0.0.1-SNAPSHOT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OAuthLogoutResponseDTO {

    /**
     * 令牌是否成功撤销
     */
    private Boolean tokenRevoked;

    /**
     * 创建登出成功响应
     *
     * @param tokenRevoked 令牌是否成功撤销
     * @return OAuthLogoutResponseDTO
     */
    public static OAuthLogoutResponseDTO of(Boolean tokenRevoked) {
        return OAuthLogoutResponseDTO.builder()
                .tokenRevoked(tokenRevoked)
                .build();
    }
}
