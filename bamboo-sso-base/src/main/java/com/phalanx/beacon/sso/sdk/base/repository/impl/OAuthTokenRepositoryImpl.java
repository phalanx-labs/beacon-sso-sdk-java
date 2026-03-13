package com.phalanx.beacon.sso.sdk.base.repository.impl;

import com.phalanx.beacon.sso.sdk.base.constant.SsoCacheConstants;
import com.phalanx.beacon.sso.sdk.base.models.OAuthToken;
import com.phalanx.beacon.sso.sdk.base.repository.OAuthTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * OAuth Token Repository 实现类
 * <p>
 * 使用 Spring Cache 注解实现 OAuth Token 的缓存管理。
 * 支持令牌的存储、查询、删除以及访问令牌的便捷获取。
 * </p>
 *
 * @author xiao_lfeng
 * @since 1.0.0
 */
@Slf4j
@Repository
public class OAuthTokenRepositoryImpl implements OAuthTokenRepository {

    /**
     * {@inheritDoc}
     */
    @Override
    @CachePut(cacheNames = SsoCacheConstants.CACHE_OAUTH_TOKEN, key = "#key")
    public OAuthToken save(String key, OAuthToken token) {
        log.debug("Saving OAuth token for key: {}", key);
        return token;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(cacheNames = SsoCacheConstants.CACHE_OAUTH_TOKEN, key = "#key")
    public Optional<OAuthToken> findByKey(String key) {
        log.debug("Finding OAuth token for key: {}", key);
        // 缓存未命中时返回空，由调用方处理
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(cacheNames = SsoCacheConstants.CACHE_OAUTH_TOKEN, key = "#key")
    public void delete(String key) {
        log.debug("Deleting OAuth token for key: {}", key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> getAccessToken(String key) {
        return this.findByKey(key)
                .map(OAuthToken::getAccessToken);
    }
}
