package com.frontleaves.phalanx.beacon.sso.sdk.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

/**
 * Beacon SSO 主配置属性类
 * <p>
 * 提供完整的 SSO 客户端配置，包括 OAuth 端点、gRPC 连接等。
 * 配置前缀: {@code beacon.sso}
 * </p>
 *
 * @author Xiao Lfeng
 * @since 0.0.1
 */
@Data
@ConfigurationProperties(prefix = "beacon.sso")
public class BeaconSsoProperties {

    /**
     * 是否启用 SSO 功能
     * <p>
     * 默认值: {@code true}
     * </p>
     */
    private boolean enabled = true;

    /**
     * SSO 服务器基础 URL
     * <p>
     * 示例: {@code https://sso.example.com}
     * </p>
     */
    private String baseUrl;

    /**
     * OAuth 客户端 ID
     * <p>
     * 在 SSO 服务端注册的应用唯一标识
     * </p>
     */
    private String clientId;

    /**
     * OAuth 客户端密钥
     * <p>
     * 用于客户端认证的机密信息，请妥善保管
     * </p>
     */
    private String clientSecret;

    /**
     * OAuth 回调地址
     * <p>
     * 授权码模式下的重定向 URI
     * </p>
     */
    private String redirectUri;

    /**
     * 排除的 URL 路径列表
     * <p>
     * 这些路径将跳过 SSO 认证检查
     * </p>
     */
    private List<String> excludeUrls;

    /**
     * Well-Known 发现地址
     * <p>
     * 用于自动发现 OAuth 服务端配置的 URI
     * </p>
     */
    private String wellKnownUri;

    /**
     * OAuth 端点配置
     * <p>
     * 嵌套配置，定义各 OAuth 端点路径
     * </p>
     */
    @NestedConfigurationProperty
    private OAuthEndpointsProperties endpoints = new OAuthEndpointsProperties();

    /**
     * gRPC 连接配置
     * <p>
     * 嵌套配置，用于 gRPC 方式的 SSO 通信
     * </p>
     */
    @NestedConfigurationProperty
    private GrpcProperties grpc = new GrpcProperties();
}
