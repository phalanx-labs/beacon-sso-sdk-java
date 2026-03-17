package com.frontleaves.phalanx.beacon.sso.sdk.springboot.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送注册邮箱验证码请求
 * <p>
 * 用于请求向指定邮箱地址发送注册验证码的请求参数。
 * 该验证码将用于后续的邮箱注册流程验证。
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
public class SendRegisterEmailCodeRequest {

    /**
     * 目标邮箱地址
     */
    @NotBlank(message = "缺少邮箱地址")
    @Email(message = "邮箱地址格式不正确")
    private String email;
}
