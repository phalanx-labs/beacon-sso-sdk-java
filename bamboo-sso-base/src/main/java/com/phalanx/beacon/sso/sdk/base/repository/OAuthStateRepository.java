package com.phalanx.beacon.sso.sdk.base.repository;

import com.phalanx.beacon.sso.sdk.base.models.OAuthState;

import java.util.Optional;

/**
 * OAuth State 管理接口
 * <p>
 * 提供 OAuth 授权流程中 State 参数的存储、查询和删除功能。
 * State 参数用于防止 CSRF 攻击，确保授权请求的完整性。
 * </p>
 *
 * @author xiao_lfeng
 * @since 1.0.0
 */
public interface OAuthStateRepository {

    /**
     * 保存 OAuth State 信息
     *
     * @param state      状态值，作为缓存键
     * @param oauthState OAuth State 对象
     * @return 保存的 OAuth State 对象
     */
    OAuthState save(String state, OAuthState oauthState);

    /**
     * 根据状态值查询 OAuth State 信息
     *
     * @param state 状态值
     * @return OAuth State 对象，如果不存在则返回空的 Optional
     */
    Optional<OAuthState> findByState(String state);

    /**
     * 根据状态值删除 OAuth State 信息
     *
     * @param state 状态值
     */
    void delete(String state);

    /**
     * 检查指定状态值是否存在
     *
     * @param state 状态值
     * @return 如果存在返回 {@code true}，否则返回 {@code false}
     */
    boolean exists(String state);
}
