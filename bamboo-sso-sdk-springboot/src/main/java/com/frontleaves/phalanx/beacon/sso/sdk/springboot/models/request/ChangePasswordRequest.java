package com.frontleaves.phalanx.beacon.sso.sdk.springboot.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改密码请求
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
    private String userId;

    /**
     * 旧密码（可选）
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;
}
