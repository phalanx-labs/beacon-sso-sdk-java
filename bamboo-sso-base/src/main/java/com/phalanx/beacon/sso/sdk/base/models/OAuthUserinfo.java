package com.phalanx.beacon.sso.sdk.base.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * OAuth 2.0 用户信息模型
 * <p>
 * 表示从用户信息端点获取的用户基本信息，遵循 OpenID Connect 标准声明。
 * 包含用户的唯一标识、名称、邮箱、头像等信息。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1-SNAPSHOT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthUserinfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一标识
     * <p>
     * 用户的唯一标识符，在授权服务器范围内唯一。
     * 对应 OpenID Connect 的 "sub" 声明。
     * </p>
     */
    @JsonProperty("sub")
    private String sub;

    /**
     * 用户名称
     * <p>
     * 用户的完整显示名称。
     * </p>
     */
    @JsonProperty("name")
    private String name;

    /**
     * 首选用户名
     * <p>
     * 用户希望被引用的用户名，通常是登录名。
     * </p>
     */
    @JsonProperty("preferred_username")
    private String preferredUsername;

    /**
     * 电子邮箱
     * <p>
     * 用户的电子邮件地址。
     * </p>
     */
    @JsonProperty("email")
    private String email;

    /**
     * 邮箱已验证标志
     * <p>
     * 标识用户邮箱地址是否已通过验证。
     * </p>
     */
    @JsonProperty("email_verified")
    private Boolean emailVerified;

    /**
     * 头像图片 URL
     * <p>
     * 用户头像图片的 URL 地址。
     * </p>
     */
    @JsonProperty("picture")
    private String picture;

    /**
     * 个人资料页面 URL
     * <p>
     * 用户个人资料页面的 URL。
     * </p>
     */
    @JsonProperty("profile")
    private String profile;

    /**
     * 语言区域设置
     * <p>
     * 用户的语言和区域设置，如 "zh-CN"、"en-US"。
     * </p>
     */
    @JsonProperty("locale")
    private String locale;

    /**
     * 更新时间戳
     * <p>
     * 用户信息最后更新的时间戳（秒）。
     * </p>
     */
    @JsonProperty("updated_at")
    private Long updatedAt;
}
