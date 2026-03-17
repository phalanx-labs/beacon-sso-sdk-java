package com.frontleaves.phalanx.beacon.sso.sdk.springboot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.aspect.InjectDataAspect;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.aspect.PermissionAspect;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller.AccountController;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller.AuthController;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller.MerchantController;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller.PublicController;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller.UserController;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.filter.BeaconSsoFilter;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.logic.AuthLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.logic.UserLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.repository.OAuthStateRepository;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.repository.OAuthTokenRepository;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.repository.UserinfoRepository;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.repository.impl.OAuthStateRepositoryImpl;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.repository.impl.OAuthTokenRepositoryImpl;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.repository.impl.UserinfoRepositoryImpl;
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
        BeaconSsoCacheConfiguration.class,
        com.frontleaves.phalanx.beacon.sso.sdk.base.config.BeaconSsoAutoConfiguration.class
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
        return new OAuthStateRepositoryImpl();
    }

    /**
     * 注册 OAuthTokenRepository Bean
     *
     * @return OAuthTokenRepository 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuthTokenRepository oauthTokenRepository() {
        return new OAuthTokenRepositoryImpl();
    }

    /**
     * 注册 UserinfoRepository Bean
     *
     * @return UserinfoRepository 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public UserinfoRepository userinfoRepository() {
        return new UserinfoRepositoryImpl();
    }

    // ==================== Logic ====================

    /**
     * 注册 AuthLogic Bean
     *
     * @param properties      SSO 配置属性
     * @param authApi         HTTP OAuth 客户端
     * @param stateRepository OAuth State 存储库
     * @param tokenRepository OAuth Token 存储库
     * @return AuthLogic 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthLogic authLogic(BeaconSsoProperties properties,
                               com.frontleaves.phalanx.beacon.sso.sdk.base.client.AuthApi authApi,
                               OAuthStateRepository stateRepository,
                               OAuthTokenRepository tokenRepository) {
        return new AuthLogic(properties, authApi, stateRepository, tokenRepository);
    }

    /**
     * 注册 UserLogic Bean
     *
     * @param userApi         HTTP User 客户端
     * @param userinfoClient  用户信息客户端（SPI）
     * @param userinfoRepository 用户信息缓存
     * @return UserLogic 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public UserLogic userLogic(com.frontleaves.phalanx.beacon.sso.sdk.base.client.UserApi userApi,
                               com.frontleaves.phalanx.beacon.sso.sdk.base.client.UserinfoClient userinfoClient,
                               UserinfoRepository userinfoRepository) {
        return new UserLogic(userApi, userinfoClient, userinfoRepository);
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
     * @param authLogic         认证逻辑处理类
     * @param userLogic         用户业务逻辑处理类
     * @param tokenRepository   令牌存储库
     * @param userinfoRepository 用户信息存储库
     * @return AuthController 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthController authController(
            AuthLogic authLogic,
            UserLogic userLogic,
            OAuthTokenRepository tokenRepository,
            UserinfoRepository userinfoRepository
    ) {
        return new AuthController(authLogic, userLogic, tokenRepository, userinfoRepository);
    }

    /**
     * 创建 UserController Bean
     * <p>
     * 仅在启用 gRPC 且存在 SsoRequest Bean 时创建。
     * </p>
     *
     * @param ssoRequest SSO 统一请求门面
     * @return UserController 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SsoRequest.class)
    public UserController userController(SsoRequest ssoRequest) {
        return new UserController(ssoRequest);
    }

    /**
     * 创建 AccountController Bean
     * <p>
     * 仅在启用 gRPC 且存在 SsoRequest Bean 时创建。
     * </p>
     *
     * @param ssoRequest SSO 统一请求门面
     * @return AccountController 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SsoRequest.class)
    public AccountController accountController(SsoRequest ssoRequest) {
        return new AccountController(ssoRequest);
    }

    /**
     * 创建 PublicController Bean
     * <p>
     * 仅在启用 gRPC 且存在 SsoRequest Bean 时创建。
     * </p>
     *
     * @param ssoRequest SSO 统一请求门面
     * @return PublicController 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SsoRequest.class)
    public PublicController publicController(SsoRequest ssoRequest) {
        return new PublicController(ssoRequest);
    }

    /**
     * 创建 MerchantController Bean
     * <p>
     * 仅在启用 gRPC 且存在 SsoRequest Bean 时创建。
     * </p>
     *
     * @param ssoRequest SSO 统一请求门面
     * @return MerchantController 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SsoRequest.class)
    public MerchantController merchantController(SsoRequest ssoRequest) {
        return new MerchantController(ssoRequest);
    }
}
