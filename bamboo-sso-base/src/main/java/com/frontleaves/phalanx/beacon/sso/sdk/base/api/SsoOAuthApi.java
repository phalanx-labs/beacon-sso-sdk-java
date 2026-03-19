package com.frontleaves.phalanx.beacon.sso.sdk.base.api;

import com.frontleaves.phalanx.beacon.sso.sdk.base.api.http.SsoHttpOAuthClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.account.RevokeTokenRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.oauth.AuthorizationUrlRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.oauth.ExchangeCodeRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.oauth.IntrospectTokenRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.oauth.RefreshTokenRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.oauth.ValidateTokenRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.oauth.AuthorizationUrlResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.oauth.IntrospectResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.oauth.TokenResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.oauth.ValidateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * OAuth 2.0 协议聚合层（HTTP-only）
 * <p>
 * 封装 OAuth 2.0 协议相关操作，包括授权 URL 生成、授权码交换、
 * 令牌刷新、撤销、自省和验证。所有方法均委托给 {@link SsoHttpOAuthClient}，
 * 通过 HTTP 协议与 SSO 服务通信。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RequiredArgsConstructor
public class SsoOAuthApi {

    private final SsoHttpOAuthClient httpClient;

    /**
     * 生成授权 URL
     */
    public AuthorizationUrlResult generateAuthorizationUrl(AuthorizationUrlRequest request) {
        log.debug("[聚合层] 生成授权 URL");
        return httpClient.generateAuthorizationUrlSdk(request);
    }

    /**
     * 使用授权码交换令牌
     */
    public Mono<TokenResult> exchangeCodeForToken(ExchangeCodeRequest request) {
        log.debug("[聚合层] 使用授权码交换令牌");
        return httpClient.exchangeCodeForTokenSdk(request);
    }

    /**
     * 刷新令牌
     */
    public Mono<TokenResult> refreshToken(RefreshTokenRequest request) {
        log.debug("[聚合层] 刷新令牌");
        return httpClient.refreshTokenSdk(request);
    }

    /**
     * 撤销令牌
     */
    public Mono<Boolean> revokeToken(RevokeTokenRequest request) {
        log.debug("[聚合层] 撤销令牌");
        return httpClient.revokeTokenSdk(request);
    }

    /**
     * 令牌自省
     */
    public Mono<IntrospectResult> introspectToken(IntrospectTokenRequest request) {
        log.debug("[聚合层] 令牌自省");
        return httpClient.introspectTokenSdk(request);
    }

    /**
     * 验证令牌有效性
     */
    public Mono<ValidateResult> validateToken(ValidateTokenRequest request) {
        log.debug("[聚合层] 验证令牌有效性");
        return httpClient.validateTokenSdk(request);
    }
}
