package com.frontleaves.phalanx.beacon.sso.sdk.base.config;

import com.frontleaves.phalanx.beacon.sso.sdk.base.api.BusinessApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.OAuthApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.base.BaseBusinessApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.base.BaseOAuthApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.logic.BusinessLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.base.logic.OAuthLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.base.repository.OAuthStateRepository;
import com.frontleaves.phalanx.beacon.sso.sdk.base.repository.OAuthTokenRepository;
import com.frontleaves.phalanx.beacon.sso.sdk.base.repository.UserinfoRepository;
import com.frontleaves.phalanx.beacon.sso.sdk.base.repository.impl.OAuthStateRepositoryImpl;
import com.frontleaves.phalanx.beacon.sso.sdk.base.repository.impl.OAuthTokenRepositoryImpl;
import com.frontleaves.phalanx.beacon.sso.sdk.base.repository.impl.UserinfoRepositoryImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Beacon SSO Bean 注册配置类
 * <p>
 * 通过 {@code @Bean} 方法注册所有 SDK 核心组件（Service / Repository / API），
 * 消除消费者手动 {@code @ComponentScan} SDK 包路径的需求。
 * </p>
 * <p>
 * 所有 Bean 均使用 {@code @ConditionalOnMissingBean}，允许消费者自定义覆盖。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Configuration
public class BeaconSsoBeanConfiguration {

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
     * 注册 OAuthLogic Bean
     *
     * @param properties      SSO 配置属性
     * @param ssoClient       SSO 统一 HTTP 客户端
     * @param stateRepository OAuth State 存储库
     * @return OAuthLogic 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuthLogic oAuthLogic(BeaconSsoProperties properties, SsoClient ssoClient,
                                  OAuthStateRepository stateRepository) {
        return new OAuthLogic(properties, ssoClient, stateRepository);
    }

    /**
     * 注册 BusinessLogic Bean
     *
     * @param properties SSO 配置属性
     * @param ssoClient  SSO 统一 HTTP 客户端
     * @return BusinessLogic 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public BusinessLogic businessLogic(BeaconSsoProperties properties, SsoClient ssoClient) {
        return new BusinessLogic(properties, ssoClient);
    }

    // ==================== Base API ====================

    /**
     * 注册 BaseOAuthApi Bean
     *
     * @param oAuthLogic     OAuth 逻辑处理类
     * @param tokenRepository 令牌存储库
     * @return BaseOAuthApi 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public BaseOAuthApi baseOAuthApi(OAuthLogic oAuthLogic, OAuthTokenRepository tokenRepository) {
        return new BaseOAuthApi(oAuthLogic, tokenRepository);
    }

    /**
     * 注册 BaseBusinessApi Bean
     *
     * @param businessLogic     业务逻辑处理类
     * @param userinfoRepository 用户信息存储库
     * @return BaseBusinessApi 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public BaseBusinessApi baseBusinessApi(BusinessLogic businessLogic,
                                           UserinfoRepository userinfoRepository) {
        return new BaseBusinessApi(businessLogic, userinfoRepository);
    }

    // ==================== Blocking API ====================

    /**
     * 注册 OAuthApi Bean（阻塞式，适配 Servlet）
     *
     * @param baseOAuthApi 响应式 OAuth API
     * @return OAuthApi 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuthApi oAuthApi(BaseOAuthApi baseOAuthApi) {
        return new OAuthApi(baseOAuthApi);
    }

    /**
     * 注册 BusinessApi Bean（阻塞式，适配 Servlet）
     *
     * @param baseBusinessApi 响应式业务 API
     * @return BusinessApi 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public BusinessApi businessApi(BaseBusinessApi baseBusinessApi) {
        return new BusinessApi(baseBusinessApi);
    }
}
