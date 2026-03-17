package com.frontleaves.phalanx.beacon.sso.sdk.springboot.repository.impl;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoCacheConstants;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthUserinfo;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.repository.UserinfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.Optional;

/**
 * 用户信息 Repository 实现类
 * <p>
 * 使用 Spring Cache 注解实现用户信息的缓存管理。
 * 以 access token 作为键缓存用户信息，减少对 UserInfo Endpoint 的请求。
 * </p>
 *
 * @author xiao_lfeng
 * @since 1.0.0
 */
@Slf4j
public class UserinfoRepositoryImpl implements UserinfoRepository {

    /**
     * {@inheritDoc}
     */
    @Override
    @CachePut(cacheNames = SsoCacheConstants.CACHE_USERINFO, key = "#accessToken")
    public OAuthUserinfo save(String accessToken, OAuthUserinfo userinfo) {
        log.debug("Saving userinfo for access token: {}", maskToken(accessToken));
        return userinfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(cacheNames = SsoCacheConstants.CACHE_USERINFO, key = "#accessToken")
    public Optional<OAuthUserinfo> findByAccessToken(String accessToken) {
        log.debug("Finding userinfo for access token: {}", maskToken(accessToken));
        // 缓存未命中时返回空，由调用方处理
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(cacheNames = SsoCacheConstants.CACHE_USERINFO, key = "#accessToken")
    public void delete(String accessToken) {
        log.debug("Deleting userinfo for access token: {}", maskToken(accessToken));
    }

    /**
     * 对 Token 进行脱敏处理，仅显示前8位
     *
     * @param token 原始 token
     * @return 脱敏后的 token
     */
    private String maskToken(String token) {
        if (token == null || token.length() <= 8) {
            return "****";
        }
        return token.substring(0, 8) + "...";
    }
}
