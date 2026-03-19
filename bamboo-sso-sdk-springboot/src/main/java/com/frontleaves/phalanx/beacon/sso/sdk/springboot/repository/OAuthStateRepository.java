package com.frontleaves.phalanx.beacon.sso.sdk.springboot.repository;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoCacheConstants;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.entity.OAuthState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.Optional;

/**
 * OAuth State Repository 实现类
 * <p>
 * 使用 Spring Cache 注解实现 OAuth State 的缓存管理。
 * 支持状态的存储、查询、删除和存在性检查。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.2
 */
@Slf4j
public class OAuthStateRepository {

    /**
     * 保存 OAuth State 信息到缓存中，用于防止 CSRF 攻击和 PKCE 验证。
     *
     * @param state       OAuth state 参数，作为缓存的主键
     * @param oauthState  要缓存的 OAuth State 信息对象
     * @return 已保存的 OAuthState 对象
     */
    @CachePut(cacheNames = SsoCacheConstants.CACHE_OAUTH_STATE, key = "#state")
    public OAuthState save(String state, OAuthState oauthState) {
        log.debug("Saving OAuth state: {}", maskState(state));
        return oauthState;
    }

    /**
     * 根据指定的 state 参数查询对应的 OAuth State 信息。
     *
     * @param state 要查询的 OAuth state 参数
     * @return 包含 OAuth State 信息的 Optional，如果缓存未命中则返回空的 Optional
     */
    @Cacheable(cacheNames = SsoCacheConstants.CACHE_OAUTH_STATE, key = "#state")
    public Optional<OAuthState> findByState(String state) {
        log.debug("Finding OAuth state: {}", maskState(state));
        // 缓存未命中时返回空，由调用方处理
        return Optional.empty();
    }

    /**
     * 删除指定的 OAuth State 缓存记录
     *
     * @param state 要删除的 OAuth state 参数
     */
    @CacheEvict(cacheNames = SsoCacheConstants.CACHE_OAUTH_STATE, key = "#state")
    public void delete(String state) {
        log.debug("Deleting OAuth state: {}", maskState(state));
    }

    /**
     * 检查指定的 OAuth State 是否存在于缓存中。
     *
     * @param state 要检查的 OAuth State 参数
     * @return 如果存在则返回 {@code true}，否则返回 {@code false}
     */
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
