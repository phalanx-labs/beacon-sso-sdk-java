package com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 邮箱注册请求 DTO
 * <p>
 * 封装邮箱注册所需的参数，包括邮箱、密码、用户名和验证码。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterEmailRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 797088222042524760L;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户名
     */
    private String username;

    /**
     * 验证码
     */
    private String verifyCode;
}
