package com.frontleaves.phalanx.beacon.sso.sdk.base.api.base;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoCacheConstants;
import com.frontleaves.phalanx.beacon.sso.sdk.base.logic.BusinessLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthIntrospection;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthUserinfo;
import com.frontleaves.phalanx.beacon.sso.sdk.base.repository.UserinfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import reactor.core.publisher.Mono;

/**
 * 响应式业务 API（带缓存）
 * <p>
 * 提供 SSO 相关的业务功能，包括获取用户信息、令牌自省和令牌验证。
 * 使用 Spring Cache 注解实现结果缓存，减少对远程服务的请求次数。
 * </p>
 *
 * @author xiao_lfeng
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class BaseBusinessApi {

    private final BusinessLogic businessLogic;
    private final UserinfoRepository userinfoRepository;

    /**
     * 获取用户信息
     * <p>
     * 使用访问令牌从用户信息端点获取已认证用户的详细信息。
     * 结果会被缓存以减少对 UserInfo Endpoint 的请求。
     * </p>
     *
     * @param accessToken 访问令牌
     * @return OAuthUserinfo 用户信息
     */
    @Cacheable(
            cacheManager = SsoCacheConstants.CACHE_MANAGER_NAME,
            cacheNames = SsoCacheConstants.CACHE_USERINFO,
            key = "#accessToken"
    )
    public Mono<OAuthUserinfo> getUserinfo(String accessToken) {
        log.debug("Fetching userinfo with access token");
        return businessLogic.getUserinfo(accessToken)
                .doOnNext(userinfo -> {
                    // 同时保存到 Repository 便于手动管理
                    userinfoRepository.save(accessToken, userinfo);
                    log.debug("Userinfo cached for access token");
                });
    }

    /**
     * 令牌自省
     * <p>
     * 调用令牌自省端点（RFC 7662）获取令牌的详细元数据信息。
     * 结果会被缓存以减少对自省端点的请求。
     * </p>
     *
     * @param token 要自省的令牌
     * @return OAuthIntrospection 令牌自省结果
     */
    @Cacheable(
            cacheManager = SsoCacheConstants.CACHE_MANAGER_NAME,
            cacheNames = SsoCacheConstants.CACHE_INTROSPECTION,
            key = "#token"
    )
    public Mono<OAuthIntrospection> introspectToken(String token) {
        log.debug("Introspecting token");
        return businessLogic.introspectToken(token);
    }

    /**
     * 验证令牌有效性
     * <p>
     * 通过令牌自省端点验证令牌是否有效且处于活跃状态。
     * 验证结果不会被缓存（布尔值），以确保每次验证都是实时的。
     * </p>
     *
     * @param token 要验证的令牌
     * @return 如果令牌有效返回 {@code true}，否则返回 {@code false}
     */
    public Mono<Boolean> validateToken(String token) {
        log.debug("Validating token");
        return businessLogic.validateToken(token);
    }
}
