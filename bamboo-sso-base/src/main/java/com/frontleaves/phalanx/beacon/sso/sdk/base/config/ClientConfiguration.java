package com.frontleaves.phalanx.beacon.sso.sdk.base.config;

import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoWebClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    /**
     * 创建 SsoWebClient Bean
     * <p>
     * 提供统一的 SSO HTTP 客户端，封装 OAuth 和 UserInfo 专用 WebClient。
     * 如果用户已自定义 SsoWebClient Bean，则不创建。
     * </p>
     *
     * @param properties SSO 配置属性
     * @return 配置好的 SsoWebClient 实例
     */
    @Bean("ssoWebClient")
    @ConditionalOnMissingBean(SsoWebClient.class)
    public SsoWebClient ssoClient(BeaconSsoProperties properties) {
        return new SsoWebClient(properties);
    }
}
