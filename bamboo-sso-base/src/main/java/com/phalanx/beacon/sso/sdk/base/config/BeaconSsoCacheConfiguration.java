package com.phalanx.beacon.sso.sdk.base.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.phalanx.beacon.sso.sdk.base.constant.SsoCacheConstants;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Beacon SSO 缓存配置类
 * <p>
 * 使用 Caffeine 作为缓存实现，配置不同缓存区域的 TTL 策略：
 * <ul>
 *   <li>oauthState: OAuth state 参数缓存，15分钟过期</li>
 *   <li>oauthToken: OAuth 令牌缓存，TTL 根据 expires_in 动态设置</li>
 *   <li>userinfo: 用户信息缓存，60秒过期</li>
 *   <li>introspection: Token 内省结果缓存，60秒过期</li>
 * </ul>
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Configuration
@EnableCaching
public class BeaconSsoCacheConfiguration {

    /**
     * 创建 SSO 缓存管理器
     * <p>
     * 使用 Caffeine 作为底层缓存实现，配置多个缓存区域以支持不同的业务场景。
     * </p>
     *
     * @return 配置好的 CaffeineCacheManager 实例
     */
    @Bean(SsoCacheConstants.CACHE_MANAGER_NAME)
    public CacheManager beaconSsoCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // 注册缓存区域
        cacheManager.setCacheNames(java.util.List.of(
                SsoCacheConstants.CACHE_OAUTH_STATE,
                SsoCacheConstants.CACHE_OAUTH_TOKEN,
                SsoCacheConstants.CACHE_USERINFO,
                SsoCacheConstants.CACHE_INTROSPECTION
        ));

        // 配置 OAuth State 缓存: 15分钟过期
        cacheManager.registerCustomCache(
                SsoCacheConstants.CACHE_OAUTH_STATE,
                Caffeine.newBuilder()
                        .expireAfterWrite(SsoCacheConstants.CACHE_OAUTH_STATE_TTL, TimeUnit.SECONDS)
                        .maximumSize(10_000)
                        .build()
        );

        // 配置 OAuth Token 缓存: 基础配置，实际 TTL 通过 Expiry 接口动态设置
        // 注意: Token 缓存的 TTL 需要根据服务端返回的 expires_in 动态设置
        // 此处提供一个默认的缓存配置，实际使用时需要配合自定义的 CacheLoader 或 Expiry
        cacheManager.registerCustomCache(
                SsoCacheConstants.CACHE_OAUTH_TOKEN,
                Caffeine.newBuilder()
                        .expireAfterWrite(3600, TimeUnit.SECONDS) // 默认1小时
                        .maximumSize(10_000)
                        .build()
        );

        // 配置用户信息缓存: 60秒过期
        cacheManager.registerCustomCache(
                SsoCacheConstants.CACHE_USERINFO,
                Caffeine.newBuilder()
                        .expireAfterWrite(SsoCacheConstants.CACHE_USERINFO_TTL, TimeUnit.SECONDS)
                        .maximumSize(10_000)
                        .build()
        );

        // 配置 Token 内省缓存: 60秒过期
        cacheManager.registerCustomCache(
                SsoCacheConstants.CACHE_INTROSPECTION,
                Caffeine.newBuilder()
                        .expireAfterWrite(SsoCacheConstants.CACHE_INTROSPECTION_TTL, TimeUnit.SECONDS)
                        .maximumSize(10_000)
                        .build()
        );

        return cacheManager;
    }
}
