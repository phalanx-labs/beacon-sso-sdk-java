package com.frontleaves.phalanx.beacon.sso.sdk.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OAuth 认证状态响应
 * <p>
 * 用于封装当前用户的 OAuth 认证状态查询结果。
 * </p>
 *
 * @param authenticated 是否已认证
 * @param tokenType     令牌类型（已认证时返回）
 * @param expiresIn     剩余过期时间（秒，已认证时返回）
 * @param user          用户信息（已认证时返回）
 * @param message       状态消息（未认证时返回）
 * @author xiao_lfeng
 * @since 0.0.1-SNAPSHOT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OAuthStatus {

    /**
     * 是否已认证
     */
    private Boolean authenticated;

    /**
     * 令牌类型（已认证时返回）
     */
    private String tokenType;

    /**
     * 剩余过期时间（秒，已认证时返回）
     */
    private Long expiresIn;

    /**
     * 用户信息（已认证时返回）
     */
    private UserInfo user;

    /**
     * 状态消息（未认证时返回）
     */
    private String message;

    /**
     * 用户信息内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserInfo {

        /**
         * 用户唯一标识
         */
        private String sub;

        /**
         * 用户名称
         */
        private String name;

        /**
         * 用户名
         */
        private String preferredUsername;

        /**
         * 用户邮箱
         */
        private String email;

        /**
         * 用户头像
         */
        private String picture;
    }

    /**
     * 创建已认证状态响应
     *
     * @param tokenType 令牌类型
     * @param expiresIn 剩余过期时间
     * @param user      用户信息
     * @return OAuthStatus
     */
    public static OAuthStatus authenticated(String tokenType, Long expiresIn, UserInfo user) {
        return OAuthStatus.builder()
                .authenticated(true)
                .tokenType(tokenType)
                .expiresIn(expiresIn)
                .user(user)
                .build();
    }

    /**
     * 创建未认证状态响应
     *
     * @param message 状态消息
     * @return OAuthStatus
     */
    public static OAuthStatus notAuthenticated(String message) {
        return OAuthStatus.builder()
                .authenticated(false)
                .message(message)
                .build();
    }
}
