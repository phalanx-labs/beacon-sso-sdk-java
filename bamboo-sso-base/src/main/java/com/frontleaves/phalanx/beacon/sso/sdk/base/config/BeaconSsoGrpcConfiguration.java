package com.frontleaves.phalanx.beacon.sso.sdk.base.config;

import com.frontleaves.phalanx.beacon.sso.sdk.base.api.HttpUserinfoClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoAccountApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoMerchantApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoPublicApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoUserApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.UserinfoClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoConfigurationException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.GrpcProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.GrpcModelConverter;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.GrpcUserConverter;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Beacon SSO gRPC 自动配置类
 * <p>
 * 用于初始化 SSO gRPC 连接、统一请求门面和聚合 API Bean。
 * </p>
 * <p>
 * 当 gRPC 启用时，注册 gRPC 版本的 API 实现，
 * 使得 {@link com.frontleaves.phalanx.beacon.sso.sdk.base.config.BeaconSsoBeanConfiguration}
 * 中的 HTTP 回退实现通过 {@code @ConditionalOnMissingBean} 被跳过。
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
        GrpcProperties grpcProperties = properties.getGrpc();
        validateGrpcConfiguration(grpcProperties);
        return ManagedChannelBuilder.forAddress(grpcProperties.getHost(), grpcProperties.getPort())
                .usePlaintext()
                .build();
    }

    /**
     * 创建 SSO 统一请求门面
     *
     * @param channel    gRPC 通道
     * @param properties SSO 配置属性
     * @return SsoRequest
     */
    @Bean
    @ConditionalOnMissingBean
    public SsoRequest ssoRequest(ManagedChannel channel, @NotNull BeaconSsoProperties properties) {
        return new SsoRequest(channel, properties.getGrpc());
    }

    /**
     * 创建 Protobuf → DTO 转换器
     *
     * @return GrpcModelConverter 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public GrpcModelConverter grpcModelConverter() {
        return new GrpcModelConverter();
    }

    /**
     * 创建 User → OAuthUserinfo 转换器
     *
     * @return GrpcUserConverter 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public GrpcUserConverter grpcUserConverter() {
        return new GrpcUserConverter();
    }

    /**
     * 创建 gRPC 用户信息客户端
     * <p>
     * 注册为 {@link UserinfoClient} 类型，优先于 HTTP 实现。
     * </p>
     *
     * @param ssoRequest    SSO 统一请求门面
     * @param userConverter User → OAuthUserinfo 转换器
     * @return gRPC UserinfoClient 实例
     */
    @Bean
    @ConditionalOnMissingBean(UserinfoClient.class)
    public UserinfoClient grpcUserinfoClient(SsoRequest ssoRequest, GrpcUserConverter userConverter) {
        return accessToken -> {
            com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.User user =
                    ssoRequest.user().getCurrentUser(accessToken);
            return reactor.core.publisher.Mono.just(userConverter.convert(user));
        };
    }

    /**
     * 创建账户管理 API（gRPC 实现）
     *
     * @param ssoRequest SSO 统一请求门面
     * @param converter  Protobuf → DTO 转换器
     * @return SsoAccountApi 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SsoAccountApi ssoAccountApi(SsoRequest ssoRequest, GrpcModelConverter converter) {
        return new SsoAccountApi(ssoRequest, converter);
    }

    /**
     * 创建用户操作 API（gRPC 实现，双传输）
     *
     * @param properties    SSO 配置属性
     * @param ssoClient     SSO 统一 HTTP 客户端
     * @param ssoRequest    SSO 统一请求门面
     * @param userConverter User → OAuthUserinfo 转换器
     * @param modelConverter Protobuf → DTO 转换器
     * @return SsoUserApi 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SsoUserApi ssoUserApi(
            BeaconSsoProperties properties,
            SsoClient ssoClient,
            SsoRequest ssoRequest,
            GrpcUserConverter userConverter,
            GrpcModelConverter modelConverter
    ) {
        return new SsoUserApi(properties, ssoClient, ssoRequest, userConverter, modelConverter);
    }

    /**
     * 创建商户操作 API（gRPC 实现）
     *
     * @param ssoRequest SSO 统一请求门面
     * @param converter  Protobuf → DTO 转换器
     * @return SsoMerchantApi 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SsoMerchantApi ssoMerchantApi(SsoRequest ssoRequest, GrpcModelConverter converter) {
        return new SsoMerchantApi(ssoRequest, converter);
    }

    /**
     * 创建公共操作 API（gRPC 实现）
     *
     * @param ssoRequest SSO 统一请求门面
     * @return SsoPublicApi 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SsoPublicApi ssoPublicApi(SsoRequest ssoRequest) {
        return new SsoPublicApi(ssoRequest);
    }

    private void validateGrpcConfiguration(GrpcProperties grpcProperties) {
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
    }
}
