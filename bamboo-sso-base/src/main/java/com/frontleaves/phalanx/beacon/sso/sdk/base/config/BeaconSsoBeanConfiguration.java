package com.frontleaves.phalanx.beacon.sso.sdk.base.config;

import com.frontleaves.phalanx.beacon.sso.sdk.base.client.AuthApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.client.HttpUserinfoClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.client.UserApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.client.UserinfoClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Beacon SSO Bean 注册配置类
 * <p>
 * 通过 {@code @Bean} 方法注册 SDK 核心客户端组件，
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

    /**
     * 注册 AuthApi Bean
     *
     * @param properties SSO 配置属性
     * @param ssoClient  SSO 统一 HTTP 客户端
     * @return AuthApi 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthApi authApi(BeaconSsoProperties properties, SsoClient ssoClient) {
        return new AuthApi(properties, ssoClient);
    }

    /**
     * 注册 UserApi Bean
     *
     * @param properties SSO 配置属性
     * @param ssoClient  SSO 统一 HTTP 客户端
     * @return UserApi 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public UserApi userApi(BeaconSsoProperties properties, SsoClient ssoClient) {
        return new UserApi(properties, ssoClient);
    }

    /**
     * 注册 HttpUserinfoClient Bean（回退实现）
     * <p>
     * 仅在 gRPC 未启用时注册。若 gRPC 启用，
     * {@link BeaconSsoGrpcConfiguration} 会先注册 {@code GrpcUserinfoClient}，
     * 此处通过 {@code @ConditionalOnMissingBean(UserinfoClient.class)} 跳过。
     * </p>
     *
     * @param userApi HTTP User 客户端
     * @return HttpUserinfoClient 实例
     */
    @Bean
    @ConditionalOnMissingBean(UserinfoClient.class)
    public UserinfoClient httpUserinfoClient(UserApi userApi) {
        return new HttpUserinfoClient(userApi);
    }
}
