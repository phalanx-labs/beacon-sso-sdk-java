package com.frontleaves.phalanx.beacon.sso.sdk.springboot.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改密码请求
 * <p>
 * 用于修改用户密码的请求参数。必须提供用户 ID 和新密码，
 * 旧密码为可选字段（如果服务端要求验证旧密码则需要提供）。
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
public class ChangePasswordRequest {

    /**
     * 用户 ID
     */
    @NotBlank(message = "缺少用户 ID")
    private String userId;

    /**
     * 旧密码（可选）
     * <p>
     * 如果服务端配置要求验证旧密码，则需要提供此字段。
     * </p>
     */
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "缺少新密码")
    private String newPassword;
}
