package com.frontleaves.phalanx.beacon.sso.sdk.springboot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.config.AutoConfiguration;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.aspect.InjectDataAspect;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.aspect.PermissionAspect;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller.*;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.filter.BeaconSsoFilter;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.logic.AuthLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.logic.UserLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.repository.OAuthStateRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
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
     * 创建 AuthController Bean
     *
     * @param authLogic 认证逻辑处理类
     * @param userLogic 用户业务逻辑处理类
     * @return AuthController 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthController authController(
            AuthLogic authLogic,
            UserLogic userLogic
    ) {
        return new AuthController(authLogic, userLogic);
    }

    /**
     * 创建 UserController Bean
     *
     * @param ssoApi SSO API 门面类
     * @return UserController 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SsoApi.class)
    public UserController userController(SsoApi ssoApi) {
        return new UserController(ssoApi);
    }

    /**
     * 创建 AccountController Bean
     *
     * @param ssoApi SSO API 门面类
     * @return AccountController 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SsoApi.class)
    public AccountController accountController(SsoApi ssoApi) {
        return new AccountController(ssoApi);
    }

    /**
     * 创建 PublicController Bean
     *
     * @param ssoApi SSO API 门面类
     * @return PublicController 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SsoApi.class)
    public PublicController publicController(SsoApi ssoApi) {
        return new PublicController(ssoApi);
    }

    /**
     * 创建 MerchantController Bean
     *
     * @param ssoApi SSO API 门面类
     * @return MerchantController 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SsoApi.class)
    public MerchantController merchantController(SsoApi ssoApi) {
        return new MerchantController(ssoApi);
    }
}
