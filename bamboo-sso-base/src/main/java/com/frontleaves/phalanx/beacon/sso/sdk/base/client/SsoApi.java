package com.frontleaves.phalanx.beacon.sso.sdk.base.client;

import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoAccountApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoMerchantApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoOAuthApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoPublicApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoUserApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.grpc.SsoGrpcAuthClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.grpc.SsoGrpcMerchantClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.grpc.SsoGrpcPublicClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.grpc.SsoGrpcUserClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.http.SsoHttpOAuthClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.http.SsoHttpUserClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * SSO API 门面类，提供对 SSO 各业务模块聚合层 API 的统一访问入口。
 * <p>
 * 内部封装了 gRPC 和 HTTP 底层客户端，通过聚合层 API 向外暴露高阶的业务操作能力。
 * 所有 API 实例采用双重检查锁定进行懒加载缓存。
 *
 * @author xiao_lfeng
 * @version v0.0.1
 * @since v0.0.1
 */
@Slf4j
public class SsoApi {

    private final BeaconSsoProperties properties;
    private final SsoGrpcAuthClient grpcAuthClient;
    private final SsoGrpcMerchantClient grpcMerchantClient;
    private final SsoGrpcPublicClient grpcPublicClient;
    private final SsoGrpcUserClient grpcUserClient;
    private final SsoHttpUserClient httpUserClient;
    private final SsoHttpOAuthClient httpOAuthClient;

    // 懒加载缓存（双重检查锁定）
    private volatile SsoAccountApi accountApi;
    private volatile SsoMerchantApi merchantApi;
    private volatile SsoPublicApi publicApi;
    private volatile SsoUserApi userApi;
    private volatile SsoOAuthApi oauthApi;

    private final Object accountLock = new Object();
    private final Object merchantLock = new Object();
    private final Object publicLock = new Object();
    private final Object userLock = new Object();
    private final Object oauthLock = new Object();

    public SsoApi(BeaconSsoProperties properties, WebClient webClient, ManagedChannel channel) {
        this.properties = properties;
        this.grpcAuthClient = new SsoGrpcAuthClient(properties, channel);
        this.grpcMerchantClient = new SsoGrpcMerchantClient(properties, channel);
        this.grpcPublicClient = new SsoGrpcPublicClient(properties, channel);
        this.grpcUserClient = new SsoGrpcUserClient(properties, channel);
        this.httpUserClient = new SsoHttpUserClient(properties, webClient);
        this.httpOAuthClient = new SsoHttpOAuthClient(properties, webClient);
    }

    /**
     * 创建账户管理聚合层 API
     *
     * @return SsoAccountApi 实例
     */
    public SsoAccountApi account() {
        if (accountApi == null) {
            synchronized (accountLock) {
                if (accountApi == null) {
                    accountApi = new SsoAccountApi(properties, grpcAuthClient);
                }
            }
        }
        return accountApi;
    }

    /**
     * 创建商户操作聚合层 API
     *
     * @return SsoMerchantApi 实例
     */
    public SsoMerchantApi merchant() {
        if (merchantApi == null) {
            synchronized (merchantLock) {
                if (merchantApi == null) {
                    merchantApi = new SsoMerchantApi(properties, grpcMerchantClient);
                }
            }
        }
        return merchantApi;
    }

    /**
     * 创建公共操作聚合层 API
     *
     * @return SsoPublicApi 实例
     */
    public SsoPublicApi pub() {
        if (publicApi == null) {
            synchronized (publicLock) {
                if (publicApi == null) {
                    publicApi = new SsoPublicApi(properties, grpcPublicClient);
                }
            }
        }
        return publicApi;
    }

    /**
     * 创建用户操作聚合层 API
     *
     * @return SsoUserApi 实例
     */
    public SsoUserApi user() {
        if (userApi == null) {
            synchronized (userLock) {
                if (userApi == null) {
                    userApi = new SsoUserApi(properties, grpcUserClient, httpUserClient);
                }
            }
        }
        return userApi;
    }

    /**
     * 创建 OAuth 2.0 协议聚合层 API
     *
     * @return SsoOAuthApi 实例
     */
    public SsoOAuthApi oauth() {
        if (oauthApi == null) {
            synchronized (oauthLock) {
                if (oauthApi == null) {
                    oauthApi = new SsoOAuthApi(httpOAuthClient);
                }
            }
        }
        return oauthApi;
    }
}
