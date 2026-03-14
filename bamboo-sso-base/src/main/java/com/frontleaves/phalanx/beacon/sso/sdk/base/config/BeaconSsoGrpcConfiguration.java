package com.frontleaves.phalanx.beacon.sso.sdk.base.config;

import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoConfigurationException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.grpc.SsoGrpcAuthClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.grpc.SsoGrpcUserClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.logic.AuthLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.base.logic.UserLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.GrpcProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.AuthServiceGrpc;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.UserServiceGrpc;
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
 * 用于初始化 SSO gRPC 连接与用户服务客户端。
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
     * 创建 UserService gRPC Stub
     *
     * @param channel gRPC 通道
     * @return UserServiceBlockingStub
     */
    @Bean
    @ConditionalOnMissingBean
    public UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub(ManagedChannel channel) {
        return UserServiceGrpc.newBlockingStub(channel);
    }

    /**
     * 创建 AuthService gRPC Stub
     *
     * @param channel gRPC 通道
     * @return AuthServiceBlockingStub
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthServiceGrpc.AuthServiceBlockingStub authServiceBlockingStub(ManagedChannel channel) {
        return AuthServiceGrpc.newBlockingStub(channel);
    }

    /**
     * 创建 UserService 客户端封装
     *
     * @param userServiceBlockingStub gRPC Stub
     * @param properties              SSO 配置属性
     * @return SsoGrpcUserClient
     */
    @Bean
    @ConditionalOnMissingBean
    public SsoGrpcUserClient ssoGrpcUserClient(
            UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub,
            BeaconSsoProperties properties
    ) {
        return new SsoGrpcUserClient(userServiceBlockingStub, properties.getGrpc());
    }

    /**
     * 创建 AuthService 客户端封装
     *
     * @param authServiceBlockingStub gRPC Stub
     * @param properties              SSO 配置属性
     * @return SsoGrpcAuthClient
     */
    @Bean
    @ConditionalOnMissingBean
    public SsoGrpcAuthClient ssoGrpcAuthClient(
            AuthServiceGrpc.AuthServiceBlockingStub authServiceBlockingStub,
            BeaconSsoProperties properties
    ) {
        return new SsoGrpcAuthClient(authServiceBlockingStub, properties.getGrpc());
    }

    /**
     * 创建用户业务逻辑
     *
     * @param ssoGrpcUserClient gRPC 用户服务客户端
     * @return UserLogic
     */
    @Bean
    @ConditionalOnMissingBean
    public UserLogic userLogic(SsoGrpcUserClient ssoGrpcUserClient) {
        return new UserLogic(ssoGrpcUserClient);
    }

    /**
     * 创建认证业务逻辑
     *
     * @param ssoGrpcAuthClient gRPC 认证服务客户端
     * @return AuthLogic
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthLogic authLogic(SsoGrpcAuthClient ssoGrpcAuthClient) {
        return new AuthLogic(ssoGrpcAuthClient);
    }

    private void validateGrpcConfiguration(GrpcProperties grpcProperties) {
        if (grpcProperties == null) {
            throw new SsoConfigurationException("gRPC properties are not configured");
        }
        if (!StringUtils.hasText(grpcProperties.getHost())) {
            throw new SsoConfigurationException("gRPC host is not configured");
        }
        if (grpcProperties.getPort() <= 0) {
            throw new SsoConfigurationException("gRPC port is not configured");
        }
        if (!StringUtils.hasText(grpcProperties.getAppAccessId())) {
            throw new SsoConfigurationException("gRPC app access id is not configured");
        }
        if (!StringUtils.hasText(grpcProperties.getAppSecretKey())) {
            throw new SsoConfigurationException("gRPC app secret key is not configured");
        }
    }
}
