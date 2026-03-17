package com.frontleaves.phalanx.beacon.sso.sdk.base.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * gRPC 用户详细信息 DTO
 * <p>
 * 对应 gRPC User 消息，封装完整的用户详细信息，比 OAuthUserinfo 更丰富。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoUserDetail implements Serializable {

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
     * 手机
     */
    private String phone;

    /**
     * 头像 URL
     */
    private String avatar;

    /**
     * 性别（0/1/2）
     */
    private int gender;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 状态（0禁用/1启用）
     */
    private int status;

    /**
     * 邮箱已验证
     */
    private boolean emailVerified;

    /**
     * 手机已验证
     */
    private boolean phoneVerified;

    /**
     * 需强制重置密码
     */
    private boolean needResetPassword;

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
    private List<SsoRole> roles;
}
