package com.phalanx.beacon.sso.sdk.springboot.config;

import com.phalanx.beacon.sso.sdk.base.config.BeaconSsoAutoConfiguration;
import com.phalanx.beacon.sso.sdk.base.logic.BusinessLogic;
import com.phalanx.beacon.sso.sdk.base.logic.OAuthLogic;
import com.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.phalanx.beacon.sso.sdk.base.repository.OAuthTokenRepository;
import com.phalanx.beacon.sso.sdk.base.repository.UserinfoRepository;
import com.phalanx.beacon.sso.sdk.springboot.controller.AuthController;
import com.phalanx.beacon.sso.sdk.springboot.filter.BeaconSsoFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Beacon SSO Spring Boot 自动配置类
 * <p>
 * 为 Servlet 类型的 Web 应用提供自动配置，自动注册 SSO 相关的 Filter、Controller 和 API Bean。
 * </p>
 * <p>
    配置生效条件：
    <ul>
    *   <li>应用为 Servlet 类型的 Web 应用</li>
    *   <li>配置项 {@code beacon.sso.enabled} 为 {@code true} 时启用</li>
    *   <li>若未配置该属性，默认启用（matchIfMissing = true）</li>
 * * </ul>
 * * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "beacon.sso", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import(BeaconSsoAutoConfiguration.class)
public class BeaconSsoSpringBootAutoConfiguration {

    /**
     * 创建 OAuthApi Bean
     * <p>
     * 提供 OAuth 授权码流程相关的 API，包括生成授权 URL、处理回调、刷新令牌等功能。
     * 如果用户已经定义了自己的 OAuthApi Bean，则不创建。
     * </p>
     *
     * @param oAuthLogic OAuth 逻辑处理类
     * @return OAuthApi 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuthLogic oAuthApi(OAuthLogic oAuthLogic) {
        return oAuthLogic;
    }
    /**
     * 创建 BusinessApi Bean
     * <p>
     * 提供业务相关的 API，包括获取用户信息、令牌自省、令牌验证等功能。
     * 如果用户已经定义了自己的 BusinessApi Bean，则不创建。
     * </p>
     *
     * @param businessLogic 业务逻辑处理类
     * @return BusinessApi 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public BusinessLogic businessApi(BusinessLogic businessLogic) {
        return businessLogic;
    }
    /**
     * 创建 BeaconSsoFilter Bean
     * <p>
     * 用于拦截请求并验证 SSO 令牌的过滤器。
     * 如果用户已经定义了自己的 BeaconSsoFilter Bean,则不创建。
     * </p>
     *
     * @param businessLogic 业务逻辑处理类
     * @param properties    SSO 配置属性
     * @return BeaconSsoFilter 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public BeaconSsoFilter beaconSsoFilter(
            BusinessLogic businessLogic,
            BeaconSsoProperties properties
    ) {
        return new BeaconSsoFilter(businessLogic, properties);
    }
    /**
     * 创建 AuthController Bean
     * <p>
     * 提供 SSO 认证相关的 REST API 端点。
     * 如果用户已经定义了自己的 AuthController Bean,则不创建。
     * </p>
     *
     * @param oAuthLogic         OAuth 逻辑处理类
     * @param tokenRepository    令牌存储库
     * @param userinfoRepository 用户信息存储库
     * @return AuthController 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthController authController(
            OAuthLogic oAuthLogic,
            OAuthTokenRepository tokenRepository,
            UserinfoRepository userinfoRepository
    ) {
        return new AuthController(oAuthLogic, tokenRepository, userinfoRepository);
    }
}
