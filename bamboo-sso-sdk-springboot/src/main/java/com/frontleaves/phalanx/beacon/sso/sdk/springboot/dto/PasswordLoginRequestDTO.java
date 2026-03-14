package com.frontleaves.phalanx.beacon.sso.sdk.springboot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 密码登录请求 DTO
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasswordLoginRequestDTO {

    /**
     * 用户名/邮箱/手机号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 授权范围
     */
    private String scope;

    /**
     * 客户端 IP
     */
    private String clientIp;

    /**
     * 客户端 User-Agent
     */
    private String userAgent;
}
