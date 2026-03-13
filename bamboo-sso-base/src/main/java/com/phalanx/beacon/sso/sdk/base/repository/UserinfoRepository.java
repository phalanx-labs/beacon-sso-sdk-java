package com.phalanx.beacon.sso.sdk.base.repository;

import com.phalanx.beacon.sso.sdk.base.models.OAuthUserinfo;

import java.util.Optional;

/**
 * 用户信息缓存接口
 * <p>
 * 提供用户信息的存储、查询和删除功能。
 * 以 access token 作为键缓存用户信息，减少对 UserInfo Endpoint 的请求次数。
 * </p>
 *
 * @author xiao_lfeng
 * @since 1.0.0
 */
public interface UserinfoRepository {

    /**
     * 保存用户信息
     *
     * @param accessToken 访问令牌，作为缓存键
     * @param userinfo    用户信息对象
     * @return 保存的用户信息对象
     */
    OAuthUserinfo save(String accessToken, OAuthUserinfo userinfo);

    /**
     * 根据访问令牌查询用户信息
     *
     * @param accessToken 访问令牌
     * @return 用户信息对象，如果不存在则返回空的 Optional
     */
    Optional<OAuthUserinfo> findByAccessToken(String accessToken);

    /**
     * 根据访问令牌删除用户信息
     *
     * @param accessToken 访问令牌
     */
    void delete(String accessToken);
}
