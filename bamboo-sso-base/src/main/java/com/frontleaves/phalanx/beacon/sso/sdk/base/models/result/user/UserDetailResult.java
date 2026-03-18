package com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.user;

import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.common.RoleResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 用户详细信息结果 DTO（gRPC 扩展字段）
 * <p>
 * 包含比 UserinfoResult 更丰富的用户信息，仅通过 gRPC 获取。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户 ID
     */
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 邮箱已验证
     */
    private Boolean emailVerified;

    /**
     * 头像 URL
     */
    private String avatar;

    // ========== gRPC 扩展字段 ==========

    /**
     * 手机号
     */
    private String phone;

    /**
     * 手机已验证
     */
    private Boolean phoneVerified;

    /**
     * 性别（0/1/2）
     */
    private Integer gender;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 状态（0禁用/1启用）
     */
    private Integer status;

    /**
     * 需强制重置密码
     */
    private Boolean needResetPassword;

    /**
     * 最后登录时间
     */
    private String lastLoginAt;

    /**
     * 最后登录 IP
     */
    private String lastLoginIp;

    /**
     * 角色列表
     */
    private List<RoleResult> roles;
}
