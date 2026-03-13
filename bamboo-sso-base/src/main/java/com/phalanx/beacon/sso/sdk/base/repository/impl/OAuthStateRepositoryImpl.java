package com.phalanx.beacon.sso.sdk.base.repository.impl;

import com.phalanx.beacon.sso.sdk.base.constant.SsoCacheConstants;
import com.phalanx.beacon.sso.sdk.base.models.OAuthState;
import com.phalanx.beacon.sso.sdk.base.repository.OAuthStateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * OAuth State Repository 实现类
 * <p>
 * 使用 Spring Cache 注解实现 OAuth State 的缓存管理。
 * 支持状态的存储、查询、删除和存在性检查。
 * </p>
 *
 * @author xiao_lfeng
 * @since 1.0.0
 */
@Slf4j
@Repository
public class OAuthStateRepositoryImpl implements OAuthStateRepository {

    /**
     * {@inheritDoc}
     */
    @Override
    @CachePut(cacheNames = SsoCacheConstants.CACHE_OAUTH_STATE, key = "#state")
    public OAuthState save(String state, OAuthState oauthState) {
        log.debug("Saving OAuth state: {}", maskState(state));
        return oauthState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(cacheNames = SsoCacheConstants.CACHE_OAUTH_STATE, key = "#state")
    public Optional<OAuthState> findByState(String state) {
        log.debug("Finding OAuth state: {}", maskState(state));
        // 缓存未命中时返回空，由调用方处理
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(cacheNames = SsoCacheConstants.CACHE_OAUTH_STATE, key = "#state")
    public void delete(String state) {
        log.debug("Deleting OAuth state: {}", maskState(state));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String state) {
        return this.findByState(state).isPresent();
    }

    /**
     * 对 State 进行脱敏处理，仅显示前8位
     *
     * @param state 原始 state
     * @return 脱敏后的 state
     */
    private String maskState(String state) {
        if (state == null || state.length() <= 8) {
            return "****";
        }
        return state.substring(0, 8) + "...";
    }
}
