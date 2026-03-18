package com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.normal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 发送邮箱验证码请求 DTO
 * <p>
 * 封装发送邮箱验证码所需的参数。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailCodeRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -1081404939692596386L;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 用途（如 REGISTER、RESET_PASSWORD 等）
     */
    private String purpose;
}
