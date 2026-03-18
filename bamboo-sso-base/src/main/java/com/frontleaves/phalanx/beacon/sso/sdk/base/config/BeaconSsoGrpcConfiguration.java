package com.frontleaves.phalanx.beacon.sso.sdk.base.config;

import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoAccountApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoMerchantApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoPublicApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoUserApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.grpc.SsoGrpcAuthClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.grpc.SsoGrpcMerchantClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.grpc.SsoGrpcPublicClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.grpc.SsoGrpcUserClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.http.SsoHttpOAuthClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.http.SsoHttpUserClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoWebClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoConfigurationException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.GrpcModelConverter;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.GrpcUserConverter;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Beacon SSO gRPC 自动配置类
 * <p>
 * 用于初始化 SSO gRPC 连接、gRPC 客户端和聚合层 API Bean。
 * 当 gRPC 启用时，注册所有必要的 Bean。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Configuration
@ConditionalOnProperty(prefix = "beacon.sso.grpc", name = "enabled", havingValue = "true")
public class BeaconSsoGrpcConfiguration {

    /**
     * 创建 gRPC 通道
     *
     * @param properties SSO 配置属性
     * @return ManagedChannel
     */
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public ManagedChannel beaconSsoGrpcChannel(BeaconSsoProperties properties) {
        var grpcProperties = properties.getGrpc();

        if (grpcProperties == null) {
            throw new SsoConfigurationException("gRPC 配置未设置");
        }
        if (!StringUtils.hasText(grpcProperties.getHost())) {
            throw new SsoConfigurationException("gRPC 主机地址未配置");
        }
        if (grpcProperties.getPort() <= 0) {
            throw new SsoConfigurationException("gRPC 端口未配置");
        }
        if (!StringUtils.hasText(grpcProperties.getAppAccessId())) {
            throw new SsoConfigurationException("gRPC 应用访问 ID 未配置");
        }
        if (!StringUtils.hasText(grpcProperties.getAppSecretKey())) {
            throw new SsoConfigurationException("gRPC 应用密钥未配置");
        }

        return ManagedChannelBuilder.forAddress(grpcProperties.getHost(), grpcProperties.getPort())
                .usePlaintext()
                .build();
    }

    /**
     * 创建 SsoWebClient Bean
     *
     * @param properties SSO 配置属性
     * @return 配置好的 SsoWebClient 实例
     */
    @Bean("ssoClient")
    @ConditionalOnMissingBean(SsoWebClient.class)
    public SsoWebClient ssoClient(BeaconSsoProperties properties) {
        return new SsoWebClient(properties);
    }

    // ========== 转换器 Bean ==========

    /**
     * 创建 gRPC 模型转换器
     *
     * @return GrpcModelConverter 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public GrpcModelConverter grpcModelConverter() {
        return new GrpcModelConverter();
    }

    /**
     * 创建 gRPC 用户转换器
     *
     * @return GrpcUserConverter 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public GrpcUserConverter grpcUserConverter() {
        return new GrpcUserConverter();
    }

    // ========== gRPC 客户端 Bean ==========

    /**
     * 创建认证服务 gRPC 客户端
     *
     * @param channel    gRPC 通道
     * @param properties SSO 配置属性
     * @return SsoGrpcAuthClient 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SsoGrpcAuthClient ssoGrpcAuthClient(ManagedChannel beaconSsoGrpcChannel, BeaconSsoProperties properties) {
        return new SsoGrpcAuthClient(beaconSsoGrpcChannel, properties);
    }

    /**
     * 创建商户服务 gRPC 客户端
     *
     * @param channel    gRPC 通道
     * @param properties SSO 配置属性
     * @return SsoGrpcMerchantClient 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SsoGrpcMerchantClient ssoGrpcMerchantClient(ManagedChannel beaconSsoGrpcChannel, BeaconSsoProperties properties) {
        return new SsoGrpcMerchantClient(beaconSsoGrpcChannel, properties);
    }

    /**
     * 创建公共服务 gRPC 客户端
     *
     * @param channel    gRPC 通道
     * @param properties SSO 配置属性
     * @return SsoGrpcPublicClient 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SsoGrpcPublicClient ssoGrpcPublicClient(ManagedChannel beaconSsoGrpcChannel, BeaconSsoProperties properties) {
        return new SsoGrpcPublicClient(beaconSsoGrpcChannel, properties);
    }

    /**
     * 创建用户服务 gRPC 客户端
     *
     * @param channel    gRPC 通道
     * @param properties SSO 配置属性
     * @return SsoGrpcUserClient 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SsoGrpcUserClient ssoGrpcUserClient(ManagedChannel beaconSsoGrpcChannel, BeaconSsoProperties properties) {
        return new SsoGrpcUserClient(beaconSsoGrpcChannel, properties);
    }

    // ========== HTTP 客户端 Bean（gRPC 启用时也需要） ==========

    /**
     * 创建 HTTP OAuth 客户端
     *
     * @param properties     SSO 配置属性
     * @param ssoWebClient   WebClient 实例
     * @return SsoHttpOAuthClient 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SsoHttpOAuthClient ssoHttpOAuthClient(BeaconSsoProperties properties, SsoWebClient ssoWebClient) {
        return new SsoHttpOAuthClient(properties, ssoWebClient.createWebClient(properties));
    }

    /**
     * 创建 HTTP 用户客户端
     *
     * @param properties     SSO 配置属性
     * @param ssoWebClient   WebClient 实例
     * @return SsoHttpUserClient 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SsoHttpUserClient ssoHttpUserClient(BeaconSsoProperties properties, SsoWebClient ssoWebClient) {
        return new SsoHttpUserClient(properties, ssoWebClient.createWebClient(properties));
    }

    // ========== 聚合层 API Bean ==========

    /**
     * 创建账户管理聚合层 API
     *
     * @param grpcClient gRPC 认证客户端
     * @param converter  gRPC 模型转换器
     * @return SsoAccountApi 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SsoAccountApi ssoAccountApi(SsoGrpcAuthClient grpcClient, GrpcModelConverter converter) {
        return new SsoAccountApi(grpcClient, converter);
    }

    /**
     * 创建商户操作聚合层 API
     *
     * @param grpcClient gRPC 商户客户端
     * @param converter  gRPC 模型转换器
     * @return SsoMerchantApi 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SsoMerchantApi ssoMerchantApi(SsoGrpcMerchantClient grpcClient, GrpcModelConverter converter) {
        return new SsoMerchantApi(grpcClient, converter);
    }

    /**
     * 创建公共操作聚合层 API
     *
     * @param grpcClient gRPC 公共客户端
     * @return SsoPublicApi 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SsoPublicApi ssoPublicApi(SsoGrpcPublicClient grpcClient) {
        return new SsoPublicApi(grpcClient);
    }

    /**
     * 创建用户操作聚合层 API
     *
     * @param properties     SSO 配置属性
     * @param grpcClient     gRPC 用户客户端
     * @param httpClient     HTTP 用户客户端
     * @param converter      gRPC 模型转换器
     * @param userConverter  gRPC 用户转换器
     * @return SsoUserApi 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SsoUserApi ssoUserApi(
            BeaconSsoProperties properties,
            SsoGrpcUserClient grpcClient,
            SsoHttpUserClient httpClient,
            GrpcModelConverter converter,
            GrpcUserConverter userConverter
    ) {
        return new SsoUserApi(properties, grpcClient, httpClient, converter, userConverter);
    }
}
