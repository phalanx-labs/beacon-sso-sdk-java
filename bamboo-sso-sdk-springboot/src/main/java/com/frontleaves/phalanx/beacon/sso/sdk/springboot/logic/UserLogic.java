package com.frontleaves.phalanx.beacon.sso.sdk.springboot.logic;

import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoOAuthApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoUserApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.oauth.IntrospectTokenRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.oauth.ValidateTokenRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.oauth.IntrospectResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.oauth.ValidateResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.user.UserinfoResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 用户业务逻辑
 * <p>
 * 提供用户信息获取、令牌自省、令牌验证等业务功能。
 * 通过 {@link SsoUserApi} 获取用户信息（支持 gRPC 和 HTTP 双传输）。
 * 缓存已在 API 层（{@link SsoUserApi} 和 {@link SsoOAuthApi}）自动处理。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RequiredArgsConstructor
public class UserLogic {

    private final SsoOAuthApi ssoOAuthApi;
    private final SsoUserApi ssoUserApi;

    /**
     * 获取用户信息（双传输 + API 层缓存）
     * <p>
     * 通过 {@link SsoUserApi} 获取用户信息（gRPC 或 HTTP），
     * 缓存由 API 层自动处理。
     * </p>
     *
     * @param accessToken 访问令牌
     * @return UserinfoResult 用户信息
     */
    public Mono<UserinfoResult> getUserinfo(String accessToken) {
        return ssoUserApi.getCurrentUser(accessToken);
    }

    /**
     * 令牌自省（仅 HTTP + 缓存）
     *
     * @param token 要自省的令牌
     * @return IntrospectResult 令牌自省结果
     */
    public Mono<IntrospectResult> introspectToken(String token) {
        IntrospectTokenRequest request = IntrospectTokenRequest.builder()
                .token(token)
                .build();
        return ssoOAuthApi.introspectToken(request);
    }

    /**
     * 验证令牌有效性
     *
     * @param token 要验证的令牌
     * @return 如果令牌有效返回 {@code true}，否则返回 {@code false}
     */
    public Mono<Boolean> validateToken(String token) {
        ValidateTokenRequest request = ValidateTokenRequest.builder()
                .token(token)
                .build();
        return ssoOAuthApi.validateToken(request)
                .map(ValidateResult::getValid);
    }

    /**
     * 验证令牌并获取自省信息
     *
     * @param token 要验证的令牌
     * @return 如果令牌有效返回自省信息，否则返回空
     */
    public Mono<IntrospectResult> validateAndGetIntrospection(String token) {
        return this.introspectToken(token)
                .filter(IntrospectResult::getActive)
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
                    if (!introspection.getActive()) {
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
                    if (!introspection.getActive() || introspection.getExp() == null) {
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
