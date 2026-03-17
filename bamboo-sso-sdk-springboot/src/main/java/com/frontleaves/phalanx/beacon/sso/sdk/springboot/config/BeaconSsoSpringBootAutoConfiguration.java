package com.frontleaves.phalanx.beacon.sso.sdk.springboot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontleaves.phalanx.beacon.sso.sdk.base.config.BeaconSsoAutoConfiguration;
import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.logic.OAuthLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.base.logic.BusinessLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.base.repository.OAuthTokenRepository;
import com.frontleaves.phalanx.beacon.sso.sdk.base.repository.UserinfoRepository;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.aspect.InjectDataAspect;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.aspect.PermissionAspect;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller.AccountController;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller.AuthController;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller.MerchantController;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller.PublicController;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller.UserController;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.filter.BeaconSsoFilter;
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
@EnableAspectJAutoProxy
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "beacon.sso", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import(BeaconSsoAutoConfiguration.class)
public class BeaconSsoSpringBootAutoConfiguration {

    /**
     * 注册 InjectDataAspect Bean
     * <p>
     * 处理带有 {@code @InjectData} 注解的方法参数，
     * 从请求属性中获取 OAuthIntrospection 并注入相应字段值。
     * </p>
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
     * <p>
     * 拦截带有 {@code @PermissionVerify} 注解的方法，
     * 验证用户是否具有所需的 OAuth scope 权限。
     * </p>
     *
     * @param objectMapper JSON 序列化工具
     * @return PermissionAspect 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public PermissionAspect permissionAspect(ObjectMapper objectMapper) {
        return new PermissionAspect(objectMapper);
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

    /**
     * 创建 UserController Bean
     * <p>
     * 提供获取当前用户信息的 REST API 端点。
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
     * 提供邮箱注册、密码登录与修改密码的 REST API 端点。
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
     * 提供发送注册验证码等公开 API 端点。
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
     * 提供商户标签查询、公告获取等 REST API 端点。
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
