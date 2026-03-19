package com.frontleaves.phalanx.beacon.sso.sdk.base.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.OAuthEndpointsProperties;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.function.BiConsumer;

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
 * <p>
 * Well-Known 发现：
 * <ul>
 *   <li>当配置了 {@code beacon.sso.well-known-uri} 时，自动从该 URI 获取 OAuth 端点配置</li>
 *   <li>从元数据中解析并更新默认端点（仅当端点为默认相对路径时）</li>
 * </ul>
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
public class AutoConfiguration implements SmartInitializingSingleton {

    private final BeaconSsoProperties properties;

    public AutoConfiguration(BeaconSsoProperties properties) {
        this.properties = properties;
    }

    @Override
    public void afterSingletonsInstantiated() {
        log.info("自动配置已初始化");

        // 如果配置了 wellKnownUri，自动发现端点
        if (StringUtils.hasText(properties.getWellKnownUri())) {
            discoverAuthEndpoints();
        }
    }

    /**
     * 从 Well-Known URI 获取 OAuth 端点配置
     */
    private void discoverAuthEndpoints() {
        try {
            log.info("正在从 Well-Known URI 获取 OAuth 端点配置: {}", properties.getWellKnownUri());

            ObjectMapper objectMapper = new ObjectMapper();
            URL url = new URL(properties.getWellKnownUri());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(30_000);

            if (connection.getResponseCode() == 200) {
                Map<String, Object> wellKnown = objectMapper.readValue(
                        connection.getInputStream(),
                        new TypeReference<>() {
                        }
                );
                updateEndpointsFromWellKnown(wellKnown);
                log.info("OAuth 端点已从 Well-Known URI 发现");
            } else {
                log.warn("Well-Known URI 返回非成功状态码: {}", connection.getResponseCode());
            }
            connection.disconnect();
        } catch (Exception e) {
            log.warn("无法从 Well-Known URI 获取元数据: {}", e.getMessage());
        }
    }

    /**
     * 从 Well-Known 元数据更新端点配置
     * <p>
     * 仅当端点为默认值（相对路径）时才更新，保留用户明确配置的值
     * </p>
     *
     * @param wellKnown Well-Known 元数据
     */
    private void updateEndpointsFromWellKnown(Map<String, Object> wellKnown) {
        OAuthEndpointsProperties endpoints = properties.getEndpoints();

        // 只有在端点为默认值（相对路径）时才使用 well-known 的值
        updateIfDefault(endpoints, "authorization_endpoint", wellKnown,
                OAuthEndpointsProperties::setAuthUri);
        updateIfDefault(endpoints, "token_endpoint", wellKnown,
                OAuthEndpointsProperties::setTokenUri);
        updateIfDefault(endpoints, "userinfo_endpoint", wellKnown,
                OAuthEndpointsProperties::setUserinfoUri);
        updateIfDefault(endpoints, "introspection_endpoint", wellKnown,
                OAuthEndpointsProperties::setIntrospectionUri);
        updateIfDefault(endpoints, "revocation_endpoint", wellKnown,
                OAuthEndpointsProperties::setRevocationUri);
    }

    /**
     * 判断端点是否为默认值，并在是的情况下从 Well-Known 更新
     *
     * @param endpoints 端点配置对象
     * @param field     Well-Known 字段名
     * @param wellKnown Well-Known 元数据
     * @param setter    端点设置器
     */
    private void updateIfDefault(
            OAuthEndpointsProperties endpoints,
            @NonNull String field,
            Map<String, Object> wellKnown,
            BiConsumer<OAuthEndpointsProperties, String> setter
    ) {
        String currentValue = switch (field) {
            case "authorization_endpoint" -> endpoints.getAuthUri();
            case "token_endpoint" -> endpoints.getTokenUri();
            case "userinfo_endpoint" -> endpoints.getUserinfoUri();
            case "introspection_endpoint" -> endpoints.getIntrospectionUri();
            case "revocation_endpoint" -> endpoints.getRevocationUri();
            default -> null;
        };

        // 判断是否为默认值（相对路径）
        if (currentValue != null && currentValue.startsWith("/")) {
            Object value = wellKnown.get(field);
            if (value instanceof String uri && StringUtils.hasText(uri)) {
                setter.accept(endpoints, uri);
                log.debug("从 Well-Known 更新端点 {}: {}", field, uri);
            }
        }
    }
}
