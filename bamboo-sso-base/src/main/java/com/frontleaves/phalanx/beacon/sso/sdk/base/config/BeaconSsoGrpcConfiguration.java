package com.frontleaves.phalanx.beacon.sso.sdk.base.config;

import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoConfigurationException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.GrpcProperties;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Beacon SSO gRPC 自动配置类
 * <p>
 * 用于初始化 SSO gRPC 连接与统一请求门面。
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
        this.validateGrpcConfiguration(grpcProperties);
        return ManagedChannelBuilder.forAddress(grpcProperties.getHost(), grpcProperties.getPort())
                .usePlaintext()
                .build();
    }

    /**
     * 创建 SSO 统一请求门面
     *
     * @param channel  gRPC 通道
     * @param properties SSO 配置属性
     * @return SsoRequest
     */
    @Bean
    @ConditionalOnMissingBean
    public SsoRequest ssoRequest(ManagedChannel channel, @NotNull BeaconSsoProperties properties) {
        return new SsoRequest(channel, properties.getGrpc());
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
