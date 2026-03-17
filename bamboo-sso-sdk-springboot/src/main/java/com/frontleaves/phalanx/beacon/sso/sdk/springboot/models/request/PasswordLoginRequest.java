package com.frontleaves.phalanx.beacon.sso.sdk.springboot.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 密码登录请求
 * <p>
 * 用于通过用户名（或邮箱/手机号）和密码进行登录认证的请求参数。
 * 登录成功后将返回访问令牌和刷新令牌。
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
public class PasswordLoginRequest {

    /**
     * 用户名/邮箱/手机号
     */
    @NotBlank(message = "缺少用户名")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "缺少密码")
    private String password;

    /**
     * 授权范围
     */
    @NotBlank(message = "缺少作用域")
    private String scope;

    /**
     * 客户端 IP（可选）
     */
    private String clientIp;

    /**
     * 客户端 User-Agent（可选）
     */
    private String userAgent;
}
