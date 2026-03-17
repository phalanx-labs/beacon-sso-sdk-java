package com.frontleaves.phalanx.beacon.sso.sdk.base.config;

import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Beacon SSO 客户端配置类
 * <p>
 * 配置 SSO SDK 的统一 HTTP 客户端 {@link SsoClient}，
 * 内部封装了 OAuth 和 UserInfo 两种专用 WebClient。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Configuration
public class BeaconSsoClientConfiguration {

    /**
     * 创建 SsoClient Bean
     * <p>
     * 提供统一的 SSO HTTP 客户端，封装 OAuth 和 UserInfo 专用 WebClient。
     * 如果用户已自定义 SsoClient Bean，则不创建。
     * </p>
     *
     * @param properties SSO 配置属性
     * @return 配置好的 SsoClient 实例
     */
    @Bean("ssoClient")
    @ConditionalOnMissingBean(SsoClient.class)
    public SsoClient ssoClient(BeaconSsoProperties properties) {
        return new SsoClient(properties);
    }
}
