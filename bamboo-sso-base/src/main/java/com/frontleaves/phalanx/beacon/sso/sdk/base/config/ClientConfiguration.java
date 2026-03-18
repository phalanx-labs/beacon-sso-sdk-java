package com.frontleaves.phalanx.beacon.sso.sdk.base.config;

import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoWebClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import io.grpc.ManagedChannel;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Beacon SSO 客户端配置类
 * <p>
 * 配置 SSO SDK 的统一 HTTP 客户端 {@link SsoWebClient}，
 * 内部封装了 OAuth 和 UserInfo 两种专用 WebClient。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Configuration
public class ClientConfiguration {

    @Bean("ssoWebClient")
    @ConditionalOnMissingBean
    public WebClient ssoWebClient(BeaconSsoProperties properties) {
        return new SsoWebClient(properties).createWebClient();
    }

    /**
     * 创建 SsoApi 门面 Bean
     *
     * @param properties SSO 配置属性
     * @param webClient  WebClient 实例
     * @param channel    gRPC 通道
     * @return SsoApi 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SsoApi ssoApi(
            BeaconSsoProperties properties,
            @NotNull @Qualifier("ssoWebClient") WebClient webClient,
            ManagedChannel channel
    ) {
        return new SsoApi(properties, webClient, channel);
    }
}
