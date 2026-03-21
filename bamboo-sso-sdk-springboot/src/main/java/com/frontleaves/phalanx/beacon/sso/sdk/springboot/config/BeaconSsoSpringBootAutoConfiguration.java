package com.frontleaves.phalanx.beacon.sso.sdk.springboot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.config.AutoConfiguration;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.aspect.InjectDataAspect;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.properties.ControllerProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.aspect.PermissionAspect;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller.BeaconSsoAccountController;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller.BeaconSsoAuthController;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller.BeaconSsoMerchantController;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller.BeaconSsoPublicController;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller.BeaconSsoUserController;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.filter.BeaconSsoFilter;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.logic.AuthLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.logic.UserLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.repository.OAuthStateRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * Beacon SSO Spring Boot 自动配置类
 * <p>
 * 为 Servlet 类型的 Web 应用提供自动配置，自动注册 SSO 相关的
 * Repository、Logic、Filter、Controller 和 API Bean。
 * </p>
 * <p>
 * 配置生效条件：
 * <ul>
 *   <li>应用为 Servlet 类型的 Web 应用</li>
 *   <li>配置项 {@code beacon.sso.enabled} 为 {@code true} 时启用</li>
 *   <li>若未配置该属性，默认启用（matchIfMissing = true）</li>
 * </ul>
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Configuration
@EnableAspectJAutoProxy
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "beacon.sso", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import({
        AutoConfiguration.class
})
@EnableConfigurationProperties(ControllerProperties.class)
public class BeaconSsoSpringBootAutoConfiguration {

    // ==================== Repository ====================

    /**
     * 注册 OAuthStateRepository Bean
     *
     * @return OAuthStateRepository 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuthStateRepository oauthStateRepository() {
        return new OAuthStateRepository();
    }

    // ==================== Logic ====================

    /**
     * 注册 AuthLogic Bean
     *
     * @param properties      SSO 配置属性
     * @param ssoApi          SSO API 门面类
     * @param stateRepository OAuth State 存储库
     * @return AuthLogic 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SsoApi.class)
    public AuthLogic authLogic(
            BeaconSsoProperties properties,
            SsoApi ssoApi,
            OAuthStateRepository stateRepository
    ) {
        return new AuthLogic(properties, ssoApi, stateRepository);
    }

    /**
     * 注册 UserLogic Bean
     *
     * @param ssoApi SSO API 门面类
     * @return UserLogic 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SsoApi.class)
    public UserLogic userLogic(SsoApi ssoApi) {
        return new UserLogic(ssoApi);
    }

    // ==================== Aspect ====================

    /**
     * 注册 InjectDataAspect Bean
     *
     * @return InjectDataAspect 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public InjectDataAspect injectDataAspect() {
        return new InjectDataAspect();
    }

    /**
     * 注册 PermissionAspect Bean
     *
     * @param objectMapper JSON 序列化工具
     * @return PermissionAspect 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public PermissionAspect permissionAspect(ObjectMapper objectMapper) {
        return new PermissionAspect(objectMapper);
    }

    // ==================== Filter ====================

    /**
     * 创建 BeaconSsoFilter Bean
     *
     * @param userLogic  用户业务逻辑处理类
     * @param properties SSO 配置属性
     * @return BeaconSsoFilter 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public BeaconSsoFilter beaconSsoFilter(
            UserLogic userLogic,
            BeaconSsoProperties properties
    ) {
        return new BeaconSsoFilter(userLogic, properties);
    }

    // ==================== Controller ====================

    /**
     * 创建 BeaconSsoAuthController Bean
     * <p>
     * 可通过配置 {@code beacon.sso.controller.auth.enabled=false} 禁用
     * </p>
     *
     * @param authLogic 认证逻辑处理类
     * @param userLogic 用户业务逻辑处理类
     * @return BeaconSsoAuthController 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "beacon.sso.controller.auth", name = "enabled", havingValue = "true", matchIfMissing = true)
    public BeaconSsoAuthController beaconSsoAuthController(
            AuthLogic authLogic,
            UserLogic userLogic
    ) {
        return new BeaconSsoAuthController(authLogic, userLogic);
    }

    /**
     * 创建 BeaconSsoUserController Bean
     * <p>
     * 可通过配置 {@code beacon.sso.controller.user.enabled=false} 禁用
     * </p>
     *
     * @param ssoApi SSO API 门面类
     * @return BeaconSsoUserController 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SsoApi.class)
    @ConditionalOnProperty(prefix = "beacon.sso.controller.user", name = "enabled", havingValue = "true", matchIfMissing = true)
    public BeaconSsoUserController beaconSsoUserController(SsoApi ssoApi) {
        return new BeaconSsoUserController(ssoApi);
    }

    /**
     * 创建 BeaconSsoAccountController Bean
     * <p>
     * 可通过配置 {@code beacon.sso.controller.account.enabled=false} 禁用
     * </p>
     *
     * @param ssoApi SSO API 门面类
     * @return BeaconSsoAccountController 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SsoApi.class)
    @ConditionalOnProperty(prefix = "beacon.sso.controller.account", name = "enabled", havingValue = "true", matchIfMissing = true)
    public BeaconSsoAccountController beaconSsoAccountController(SsoApi ssoApi) {
        return new BeaconSsoAccountController(ssoApi);
    }

    /**
     * 创建 BeaconSsoPublicController Bean
     * <p>
     * 可通过配置 {@code beacon.sso.controller.public-interface.enabled=false} 禁用
     * </p>
     *
     * @param ssoApi SSO API 门面类
     * @return BeaconSsoPublicController 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SsoApi.class)
    @ConditionalOnProperty(prefix = "beacon.sso.controller.public-interface", name = "enabled", havingValue = "true", matchIfMissing = true)
    public BeaconSsoPublicController beaconSsoPublicController(SsoApi ssoApi) {
        return new BeaconSsoPublicController(ssoApi);
    }

    /**
     * 创建 BeaconSsoMerchantController Bean
     * <p>
     * 可通过配置 {@code beacon.sso.controller.merchant.enabled=false} 禁用
     * </p>
     *
     * @param ssoApi SSO API 门面类
     * @return BeaconSsoMerchantController 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SsoApi.class)
    @ConditionalOnProperty(prefix = "beacon.sso.controller.merchant", name = "enabled", havingValue = "true", matchIfMissing = true)
    public BeaconSsoMerchantController beaconSsoMerchantController(SsoApi ssoApi) {
        return new BeaconSsoMerchantController(ssoApi);
    }
}
