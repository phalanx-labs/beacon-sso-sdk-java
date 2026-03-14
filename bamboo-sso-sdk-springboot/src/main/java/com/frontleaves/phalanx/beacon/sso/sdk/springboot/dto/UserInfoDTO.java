package com.frontleaves.phalanx.beacon.sso.sdk.springboot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 当前用户信息 DTO
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoDTO {

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
     * 邮箱地址
     */
    private String email;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 头像 URL
     */
    private String avatar;

    /**
     * 性别（0 未知 / 1 男 / 2 女）
     */
    private Integer gender;

    /**
     * 生日（ISO 8601 日期格式）
     */
    private String birthday;

    /**
     * 状态（0 禁用 / 1 启用）
     */
    private Integer status;

    /**
     * 邮箱是否已验证
     */
    private Boolean emailVerified;

    /**
     * 手机是否已验证
     */
    private Boolean phoneVerified;

    /**
     * 是否需要强制重置密码
     */
    private Boolean needResetPassword;

    /**
     * 最后登录时间（ISO 8601 格式）
     */
    private String lastLoginAt;

    /**
     * 最后登录 IP
     */
    private String lastLoginIp;

    /**
     * 用户角色列表
     */
    private List<UserRoleDTO> roles;
}
