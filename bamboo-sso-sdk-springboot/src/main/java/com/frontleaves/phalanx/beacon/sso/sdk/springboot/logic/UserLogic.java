package com.frontleaves.phalanx.beacon.sso.sdk.springboot.logic;

import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoOAuthApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.UserinfoClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthIntrospection;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthUserinfo;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.repository.UserinfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * 用户业务逻辑
 * <p>
 * 提供用户信息获取、令牌自省、令牌验证等业务功能。
 * 通过 {@link UserinfoClient} SPI 实现双传输策略（gRPC 优先 / HTTP 回退）。
 * 结果通过 {@link UserinfoRepository} 进行缓存。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RequiredArgsConstructor
public class UserLogic {

    private final SsoOAuthApi ssoOAuthApi;
    private final UserinfoClient userinfoClient;
    private final UserinfoRepository userinfoRepository;

    /**
     * 获取用户信息（双传输 + 缓存）
     * <p>
     * 通过 {@link UserinfoClient} SPI 获取用户信息（gRPC 或 HTTP），
     * 结果缓存到 {@link UserinfoRepository}。
     * </p>
     *
     * @param accessToken 访问令牌
     * @return OAuthUserinfo 用户信息
     */
    public Mono<OAuthUserinfo> getUserinfo(String accessToken) {
        // 先检查缓存
        Optional<OAuthUserinfo> cached = userinfoRepository.findByAccessToken(accessToken);
        if (cached.isPresent()) {
            log.debug("从缓存获取用户信息");
            return Mono.just(cached.get());
        }

        return userinfoClient.getUserinfo(accessToken)
                .doOnNext(userinfo -> userinfoRepository.save(accessToken, userinfo));
    }

    /**
     * 令牌自省（仅 HTTP + 缓存）
     *
     * @param token 要自省的令牌
     * @return OAuthIntrospection 令牌自省结果
     */
    public Mono<OAuthIntrospection> introspectToken(String token) {
        return ssoOAuthApi.introspectToken(token);
    }

    /**
     * 验证令牌有效性
     *
     * @param token 要验证的令牌
     * @return 如果令牌有效返回 {@code true}，否则返回 {@code false}
     */
    public Mono<Boolean> validateToken(String token) {
        return ssoOAuthApi.validateToken(token);
    }

    /**
     * 验证令牌并获取自省信息
     *
     * @param token 要验证的令牌
     * @return 如果令牌有效返回自省信息，否则返回空
     */
    public Mono<OAuthIntrospection> validateAndGetIntrospection(String token) {
        return this.introspectToken(token)
                .filter(OAuthIntrospection::isActive)
                .onErrorResume(error -> {
                    log.debug("令牌验证返回为空: {}", error.getMessage());
                    return Mono.empty();
                });
    }

    /**
     * 检查令牌是否即将过期
     *
     * @param token            要检查的令牌
     * @param thresholdSeconds 过期阈值（秒）
     * @return 如果令牌即将过期返回 {@code true}，否则返回 {@code false}
     */
    public Mono<Boolean> isTokenExpiringSoon(String token, long thresholdSeconds) {
        return this.introspectToken(token)
                .map(introspection -> {
                    if (!introspection.isActive()) {
                        return true;
                    }
                    if (introspection.getExp() == null) {
                        return false;
                    }
                    long nowInSeconds = System.currentTimeMillis() / 1000;
                    long remainingSeconds = introspection.getExp() - nowInSeconds;
                    return remainingSeconds <= thresholdSeconds;
                })
                .onErrorResume(error -> {
                    log.warn("检查令牌过期时间失败: {}", error.getMessage());
                    return Mono.just(true);
                });
    }

    /**
     * 获取令牌剩余有效时间
     *
     * @param token 要检查的令牌
     * @return 剩余有效时间（秒），如果令牌无效或无法获取则返回 0
     */
    public Mono<Long> getTokenRemainingTime(String token) {
        return this.introspectToken(token)
                .map(introspection -> {
                    if (!introspection.isActive() || introspection.getExp() == null) {
                        return 0L;
                    }
                    long nowInSeconds = System.currentTimeMillis() / 1000;
                    long remainingSeconds = introspection.getExp() - nowInSeconds;
                    return Math.max(0L, remainingSeconds);
                })
                .onErrorResume(error -> {
                    log.warn("获取令牌剩余时间失败: {}", error.getMessage());
                    return Mono.just(0L);
                });
    }
}
