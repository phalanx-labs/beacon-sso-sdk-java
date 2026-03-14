package com.frontleaves.phalanx.beacon.sso.sdk.springboot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 邮箱注册响应 DTO
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterByEmailResponseDTO {

    /**
     * 用户 ID
     */
    private String userId;

    /**
     * 登录 Token
     */
    private String token;
}
