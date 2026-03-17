package com.frontleaves.phalanx.beacon.sso.sdk.base.config;

import com.frontleaves.phalanx.beacon.sso.sdk.base.api.HttpUserinfoClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoOAuthApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoUserApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.UserinfoClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.GrpcModelConverter;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.GrpcUserConverter;
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
     * 注册 OAuth HTTP API Bean
     * <p>
     * OAuth 协议操作始终通过 HTTP 执行，无条件注册。
     * </p>
     *
     * @param properties SSO 配置属性
     * @param ssoClient  SSO 统一 HTTP 客户端
     * @return SsoOAuthApi 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SsoOAuthApi ssoOAuthApi(BeaconSsoProperties properties, SsoClient ssoClient) {
        return new SsoOAuthApi(properties, ssoClient);
    }

    /**
     * 注册 Protobuf → DTO 转换器（基础 Bean）
     * <p>
     * 无论 gRPC 是否启用，都需要注册此转换器，因为 HTTP 回退版本的 SsoUserApi 也需要它。
     * </p>
     *
     * @return GrpcModelConverter 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public GrpcModelConverter grpcModelConverter() {
        return new GrpcModelConverter();
    }

    /**
     * 注册 User → OAuthUserinfo 转换器（基础 Bean）
     * <p>
     * 无论 gRPC 是否启用，都需要注册此转换器，因为 HTTP 回退版本的 SsoUserApi 也需要它。
     * </p>
     *
     * @return GrpcUserConverter 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public GrpcUserConverter grpcUserConverter() {
        return new GrpcUserConverter();
    }

    /**
     * 注册 User HTTP API Bean（回退实现）
     * <p>
     * 仅在 gRPC 未启用时注册。若 gRPC 启用，
     * {@link com.frontleaves.phalanx.beacon.sso.sdk.base.config.BeaconSsoGrpcConfiguration}
     * 会先注册 gRPC 版本，此处通过 {@code @ConditionalOnMissingBean} 跳过。
     * </p>
     *
     * @param properties     SSO 配置属性
     * @param ssoClient      SSO 统一 HTTP 客户端
     * @param userConverter  User → OAuthUserinfo 转换器
     * @param modelConverter Protobuf → DTO 转换器
     * @return SsoUserApi 实例（HTTP 回退版，ssoRequest 为 null）
     */
    @Bean
    @ConditionalOnMissingBean(SsoUserApi.class)
    public SsoUserApi ssoUserApiHttp(
            BeaconSsoProperties properties,
            SsoClient ssoClient,
            GrpcUserConverter userConverter,
            GrpcModelConverter modelConverter
    ) {
        // ssoRequest 为 null 时，SsoUserApi 会自动回退到 HTTP
        return new SsoUserApi(properties, ssoClient, null, userConverter, modelConverter);
    }

    /**
     * 注册 HttpUserinfoClient Bean（回退实现）
     * <p>
     * 仅在 gRPC 未启用时注册。若 gRPC 启用，
     * {@link com.frontleaves.phalanx.beacon.sso.sdk.base.config.BeaconSsoGrpcConfiguration}
     * 会先注册 gRPC 版本的 UserinfoClient，
     * 此处通过 {@code @ConditionalOnMissingBean(UserinfoClient.class)} 跳过。
     * </p>
     *
     * @param properties SSO 配置属性
     * @param ssoClient  SSO 统一 HTTP 客户端
     * @return HttpUserinfoClient 实例
     */
    @Bean
    @ConditionalOnMissingBean(UserinfoClient.class)
    public UserinfoClient httpUserinfoClient(BeaconSsoProperties properties, SsoClient ssoClient) {
        return new HttpUserinfoClient(properties, ssoClient);
    }
}
