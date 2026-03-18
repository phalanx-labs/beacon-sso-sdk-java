package com.frontleaves.phalanx.beacon.sso.sdk.base.config;

import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoWebClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoConfigurationException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Beacon SSO gRPC 自动配置类
 * <p>
 * 用于初始化 SSO gRPC 连接和聚合层 API Bean。
 * 当 gRPC 启用时，注册所有必要的 Bean。
 * gRPC Client 实例在聚合器内部直接创建，不单独注册为 Bean。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Configuration
@ConditionalOnProperty(prefix = "beacon.sso.grpc", name = "enabled", havingValue = "true")
public class GrpcConfiguration {

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
}
