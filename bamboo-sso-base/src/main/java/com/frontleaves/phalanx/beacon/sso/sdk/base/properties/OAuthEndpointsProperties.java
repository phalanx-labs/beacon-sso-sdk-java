package com.frontleaves.phalanx.beacon.sso.sdk.base.properties;

import lombok.Data;

/**
 * OAuth 端点配置属性类
 * <p>
 * 定义 SSO 服务器各 OAuth 2.0 端点的相对路径。
 * 实际端点 URL = {@code baseUrl} + 端点路径
 * </p>
 *
 * @author Xiao Lfeng
 * @since 0.0.1
 */
@Data
public class OAuthEndpointsProperties {

    /**
     * 授权端点路径
     * <p>
     * 用于获取授权码的端点
     * </p>
     * 默认值: {@code /oauth/authorize}
     */
    private String authUri = "/oauth/authorize";

    /**
     * 令牌端点路径
     * <p>
     * 用于交换令牌（access_token、refresh_token）的端点
     * </p>
     * 默认值: {@code /oauth/token}
     */
    private String tokenUri = "/oauth/token";

    /**
     * 用户信息端点路径
     * <p>
     * 用于获取已认证用户详细信息的端点
     * </p>
     * 默认值: {@code /oauth/userinfo}
     */
    private String userinfoUri = "/oauth/userinfo";

    /**
     * 令牌内省端点路径
     * <p>
     * 用于验证和获取令牌信息的端点
     * </p>
     * 默认值: {@code /oauth/introspect}
     */
    private String introspectionUri = "/oauth/introspect";

    /**
     * 令牌撤销端点路径
     * <p>
     * 用于撤销访问令牌或刷新令牌的端点
     * </p>
     * 默认值: {@code /oauth/revoke}
     */
    private String revocationUri = "/oauth/revoke";
}
