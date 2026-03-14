package com.frontleaves.phalanx.beacon.sso.sdk.base.api.base;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoCacheConstants;
import com.frontleaves.phalanx.beacon.sso.sdk.base.logic.OAuthLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthToken;
import com.frontleaves.phalanx.beacon.sso.sdk.base.repository.OAuthTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 响应式 OAuth API（带缓存）
 * <p>
 * 提供 OAuth 2.0 相关的响应式 API 接口，支持授权 URL 生成、回调处理、
 * 令牌刷新和撤销功能。使用 Spring Cache 注解实现结果缓存。
 * </p>
 *
 * @author xiao_lfeng
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BaseOAuthApi {

    private final OAuthLogic oAuthLogic;
    private final OAuthTokenRepository oAuthTokenRepository;

    /**
     * 生成授权 URL（含 PKCE）
     * <p>
     * 构建 OAuth 2.0 授权码流程的授权 URL，自动生成 state 参数和 PKCE 相关参数。
     * 结果会被缓存以减少重复生成。
     * </p>
     *
     * @return 授权 URL 字符串
     */
    @Cacheable(
            cacheManager = SsoCacheConstants.CACHE_MANAGER_NAME,
            cacheNames = SsoCacheConstants.CACHE_OAUTH_STATE,
            key = "'authUrl:' + T(java.util.UUID).randomUUID().toString()"
    )
    public Mono<String> generateAuthorizationUrl() {
        log.debug("Generating OAuth authorization URL with PKCE");
        return oAuthLogic.generateAuthorizationUrl();
    }

    /**
     * 处理回调，验证 state，获取 token
     * <p>
     * 处理 OAuth 授权服务器回调，验证 state 参数的有效性，
     * 使用授权码和 PKCE code_verifier 交换访问令牌。
     * 获取的 token 会被缓存。
     * </p>
     *
     * @param code  授权码
     * @param state 状态参数
     * @return OAuthToken 令牌响应
     */
    public Mono<OAuthToken> handleCallback(String code, String state) {
        log.debug("Handling OAuth callback with code and state");
        return oAuthLogic.handleCallback(code, state)
                .doOnNext(token -> {
                    // 缓存 token，使用 state 作为键
                    oAuthTokenRepository.save(state, token);
                    log.debug("OAuth token cached with state key: {}", state);
                });
    }

    /**
     * 刷新 token
     * <p>
     * 使用刷新令牌获取新的访问令牌，结果会被缓存。
     * </p>
     *
     * @param refreshToken 刷新令牌
     * @return 新的 OAuthToken 令牌响应
     */
    @Cacheable(
            cacheManager = SsoCacheConstants.CACHE_MANAGER_NAME,
            cacheNames = SsoCacheConstants.CACHE_OAUTH_TOKEN,
            key = "'refresh:' + #refreshToken"
    )
    public Mono<OAuthToken> refreshToken(String refreshToken) {
        log.debug("Refreshing OAuth token");
        return oAuthLogic.refreshToken(refreshToken)
                .doOnNext(token -> {
                    // 缓存新 token
                    oAuthTokenRepository.save("refresh:" + refreshToken, token);
                    log.debug("Refreshed token cached");
                });
    }

    /**
     * 撤销 token
     * <p>
     * 撤销访问令牌或刷新令牌，使令牌立即失效。
     * 撤销成功后会从缓存中移除相关 token。
     * </p>
     *
     * @param token 要撤销的令牌
     * @return 如果撤销成功返回 {@code true}，否则返回 {@code false}
     */
    public Mono<Boolean> revokeToken(String token) {
        log.debug("Revoking OAuth token");
        return oAuthLogic.revokeToken(token)
                .doOnNext(success -> {
                    if (success) {
                        // 撤销成功后从缓存中删除
                        oAuthTokenRepository.delete(token);
                        log.debug("Revoked token removed from cache");
                    }
                });
    }
}
