package com.frontleaves.phalanx.beacon.sso.sdk.springboot.repository;

import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthToken;

import java.util.Optional;

/**
 * OAuth Token 缓存管理接口
 * <p>
 * 提供 OAuth 令牌的存储、查询和删除功能。
 * 支持按 key 存储和获取令牌信息，便于令牌的生命周期管理。
 * </p>
 *
 * @author xiao_lfeng
 * @since 1.0.0
 */
public interface OAuthTokenRepository {

    /**
     * 保存 OAuth Token 信息
     *
     * @param key   缓存键，通常为用户标识或会话标识
     * @param token OAuth Token 对象
     * @return 保存的 OAuth Token 对象
     */
    OAuthToken save(String key, OAuthToken token);

    /**
     * 根据键查询 OAuth Token 信息
     *
     * @param key 缓存键
     * @return OAuth Token 对象，如果不存在则返回空的 Optional
     */
    Optional<OAuthToken> findByKey(String key);

    /**
     * 根据键删除 OAuth Token 信息
     *
     * @param key 缓存键
     */
    void delete(String key);

    /**
     * 根据键获取访问令牌
     * <p>
     * 便捷方法，直接返回 access_token 字符串
     * </p>
     *
     * @param key 缓存键
     * @return 访问令牌字符串，如果不存在则返回空的 Optional
     */
    Optional<String> getAccessToken(String key);
}
