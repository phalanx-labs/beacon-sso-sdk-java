package com.frontleaves.phalanx.beacon.sso.sdk.springboot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoAccountApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoMerchantApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoOAuthApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoPublicApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoUserApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.config.AutoConfiguration;
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
     * @param ssoOAuthApi     HTTP OAuth 客户端
     * @param stateRepository OAuth State 存储库
     * @return AuthLogic 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthLogic authLogic(BeaconSsoProperties properties,
                               SsoOAuthApi ssoOAuthApi,
                               OAuthStateRepository stateRepository) {
        return new AuthLogic(properties, ssoOAuthApi, stateRepository);
    }

    /**
     * 注册 UserLogic Bean
     *
     * @param ssoOAuthApi OAuth 客户端
     * @param ssoUserApi  用户操作 API
     * @return UserLogic 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public UserLogic userLogic(SsoOAuthApi ssoOAuthApi,
                               SsoUserApi ssoUserApi) {
        return new UserLogic(ssoOAuthApi, ssoUserApi);
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
     * <p>
     * 仅在启用 gRPC 且存在 SsoUserApi Bean 时创建。
     * </p>
     *
     * @param ssoUserApi 用户操作 API
     * @return UserController 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SsoUserApi.class)
    public UserController userController(SsoUserApi ssoUserApi) {
        return new UserController(ssoUserApi);
    }

    /**
     * 创建 AccountController Bean
     * <p>
     * 仅在启用 gRPC 且存在 SsoAccountApi Bean 时创建。
     * </p>
     *
     * @param ssoAccountApi 账户操作 API
     * @return AccountController 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SsoAccountApi.class)
    public AccountController accountController(SsoAccountApi ssoAccountApi) {
        return new AccountController(ssoAccountApi);
    }

    /**
     * 创建 PublicController Bean
     * <p>
     * 仅在启用 gRPC 且存在 SsoPublicApi Bean 时创建。
     * </p>
     *
     * @param ssoPublicApi 公共操作 API
     * @return PublicController 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SsoPublicApi.class)
    public PublicController publicController(SsoPublicApi ssoPublicApi) {
        return new PublicController(ssoPublicApi);
    }

    /**
     * 创建 MerchantController Bean
     * <p>
     * 仅在启用 gRPC 且存在 SsoMerchantApi Bean 时创建。
     * </p>
     *
     * @param ssoMerchantApi 商户操作 API
     * @return MerchantController 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SsoMerchantApi.class)
    public MerchantController merchantController(SsoMerchantApi ssoMerchantApi) {
        return new MerchantController(ssoMerchantApi);
    }
}
