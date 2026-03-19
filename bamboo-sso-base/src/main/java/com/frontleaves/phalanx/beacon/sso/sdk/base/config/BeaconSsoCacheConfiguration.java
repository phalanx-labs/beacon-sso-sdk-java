package com.frontleaves.phalanx.beacon.sso.sdk.base.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoCacheConstants;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Beacon SSO 缓存配置类
 *
 * @author xiao_lfeng
 * @since 0.0.2
 */
@Configuration
@EnableCaching
public class BeaconSsoCacheConfiguration {

    @Bean(SsoCacheConstants.CACHE_MANAGER_NAME)
    public CacheManager beaconSsoCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.setCacheNames(List.of(
                SsoCacheConstants.CACHE_OAUTH_STATE,
                SsoCacheConstants.CACHE_GRPC_USERINFO,
                SsoCacheConstants.CACHE_GRPC_MERCHANT_TAG,
                SsoCacheConstants.CACHE_GRPC_ANNOUNCEMENT,
                SsoCacheConstants.CACHE_HTTP_USERINFO,
                SsoCacheConstants.CACHE_HTTP_INTROSPECTION
        ));

        Caffeine<Object, Object> shortTtlCaffeine = Caffeine.newBuilder()
                .expireAfterWrite(SsoCacheConstants.CACHE_NORMAL_TTL, TimeUnit.SECONDS)
                .maximumSize(SsoCacheConstants.CACHE_MAXIMUM_SIZE);

        cacheManager.registerCustomCache(
                SsoCacheConstants.CACHE_OAUTH_STATE,
                Caffeine.newBuilder()
                        .expireAfterWrite(SsoCacheConstants.CACHE_OAUTH_STATE_TTL, TimeUnit.SECONDS)
                        .maximumSize(SsoCacheConstants.CACHE_MAXIMUM_SIZE)
                        .build()
        );

        // gRPC 缓存
        cacheManager.registerCustomCache(SsoCacheConstants.CACHE_GRPC_USERINFO, shortTtlCaffeine.build());
        cacheManager.registerCustomCache(SsoCacheConstants.CACHE_GRPC_MERCHANT_TAG, shortTtlCaffeine.build());
        cacheManager.registerCustomCache(SsoCacheConstants.CACHE_GRPC_ANNOUNCEMENT, shortTtlCaffeine.build());

        // HTTP 缓存
        cacheManager.registerCustomCache(SsoCacheConstants.CACHE_HTTP_USERINFO, shortTtlCaffeine.build());
        cacheManager.registerCustomCache(SsoCacheConstants.CACHE_HTTP_INTROSPECTION, shortTtlCaffeine.build());

        return cacheManager;
    }
}
