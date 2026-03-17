package com.frontleaves.phalanx.beacon.sso.sdk.springboot.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 邮箱注册请求
 * <p>
 * 用于通过邮箱注册新用户的请求参数。包含邮箱地址、验证码、密码等必填信息，
 * 以及用户名和昵称等可选信息。
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
public class RegisterByEmailRequest {

    /**
     * 邮箱地址
     * <p>
     * 注册所使用的邮箱地址，需通过验证码验证。
     * </p>
     */
    @NotBlank(message = "缺少邮箱地址")
    @Email(message = "邮箱地址格式不正确")
    private String email;

    /**
     * 验证码
     * <p>
     * 通过 {@code /public/register/email/code} 端点获取的邮箱验证码。
     * </p>
     */
    @NotBlank(message = "缺少验证码")
    private String code;

    /**
     * 用户名（可选）
     */
    private String username;

    /**
     * 密码
     * <p>
     * 用户设置的登录密码。
     * </p>
     */
    @NotBlank(message = "缺少密码")
    private String password;

    /**
     * 昵称（可选）
     */
    private String nickname;
}
