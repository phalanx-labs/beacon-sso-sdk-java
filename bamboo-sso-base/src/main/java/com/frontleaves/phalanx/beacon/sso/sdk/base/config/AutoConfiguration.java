package com.frontleaves.phalanx.beacon.sso.sdk.base.config;

import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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
 * <p>
 * 加载顺序：
 * <ol>
 *   <li>{@link ClientConfiguration} — SsoWebClient（WebClient）</li>
 *   <li>{@link GrpcConfiguration} — gRPC 通道 + SsoGrpcClient + gRPC API 实现（条件加载）</li>
 * </ol>
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "beacon.sso", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(BeaconSsoProperties.class)
@EnableCaching
@Import({
        BeaconSsoCacheConfiguration.class,
        ClientConfiguration.class,
        GrpcConfiguration.class,
})
public class AutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("AutoConfiguration has been initialized.");
    }
}
