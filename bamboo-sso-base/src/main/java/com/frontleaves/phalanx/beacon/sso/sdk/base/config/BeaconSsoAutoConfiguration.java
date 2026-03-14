package com.frontleaves.phalanx.beacon.sso.sdk.base.config;

import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Beacon SSO 自动配置类
 * <p>
 * 作为 SSO SDK 的主配置入口，负责导入所有必要的配置类并启用配置属性。
 * </p>
 * <p>
 * 配置生效条件：
 * <ul>
 *   <li>当配置项 {@code beacon.sso.enabled} 为 {@code true} 时启用</li>
 *   <li>若未配置该属性，默认启用（matchIfMissing = true）</li>
 * </ul>
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Configuration
@ConditionalOnProperty(prefix = "beacon.sso", name = "enabled", matchIfMissing = true)
@PropertySource("classpath:beacon-sso-defaults.properties")
@EnableConfigurationProperties(BeaconSsoProperties.class)
@Import({
        BeaconSsoCacheConfiguration.class,
        BeaconSsoClientConfiguration.class,
        BeaconSsoGrpcConfiguration.class
})
public class BeaconSsoAutoConfiguration {

    /**
     * 默认构造函数
     * <p>
     * 主配置类，通过 @Import 注解自动加载缓存和 WebClient 配置。
     * </p>
     */
    public BeaconSsoAutoConfiguration() {
        // 配置类初始化完成
    }
}
