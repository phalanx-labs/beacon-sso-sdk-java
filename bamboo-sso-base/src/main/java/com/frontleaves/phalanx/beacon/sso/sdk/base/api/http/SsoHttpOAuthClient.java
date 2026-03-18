package com.frontleaves.phalanx.beacon.sso.sdk.base.api.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoConfigurationException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.TokenException;
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
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.PkceUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

/**
 * SSO HTTP OAuth 服务客户端
 * <p>
 * 封装 OAuth 2.0 协议相关操作，通过 HTTP 协议与 SSO 服务通信。
 * 包括授权 URL 生成、授权码交换、令牌刷新、撤销、自省和验证。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
public class SsoHttpOAuthClient {

    private final BeaconSsoProperties properties;
    private final WebClient ssoWebClient;

    /**
     * 构造函数
     *
     * @param beaconSsoProperties SSO 配置属性
     * @param ssoWebClient           已配置的 WebClient 实例
     * @throws SsoConfigurationException 如果配置无效
     */
    @Contract("null, _ -> fail; !null, null -> fail")
    public SsoHttpOAuthClient(BeaconSsoProperties beaconSsoProperties, WebClient ssoWebClient) {
        if (beaconSsoProperties == null) {
            throw new SsoConfigurationException("SSO 配置未设置");
        }
        if (ssoWebClient == null) {
            throw new SsoConfigurationException("WebClient 未配置");
        }
        if (!StringUtils.hasText(beaconSsoProperties.getBaseUrl())) {
            throw new SsoConfigurationException("SSO 基础 URL 未配置");
        }
        if (!StringUtils.hasText(beaconSsoProperties.getClientId())) {
            throw new SsoConfigurationException("OAuth 客户端 ID 未配置");
        }
        if (beaconSsoProperties.getEndpoints() == null) {
            throw new SsoConfigurationException("OAuth 端点配置未设置");
        }

        this.properties = beaconSsoProperties;
        this.ssoWebClient = ssoWebClient;
    }

    /**
     * 生成授权 URL（默认 scope）
     *
     * @param state         state 参数
     * @param codeChallenge PKCE code_challenge
     * @return 授权 URL 字符串
     */
    public String generateAuthorizationUrl(String state, String codeChallenge) {
        return buildAuthorizationUrl(state, codeChallenge, "openid profile email phone");
    }

    /**
     * 生成授权 URL（自定义 scope）
     *
     * @param state         state 参数
     * @param codeChallenge PKCE code_challenge
     * @param scope         自定义作用域
     * @return 授权 URL 字符串
     */
    public String generateAuthorizationUrl(String state, String codeChallenge, String scope) {
        return buildAuthorizationUrl(state, codeChallenge,
                StringUtils.hasText(scope) ? scope : "openid profile email phone");
    }

    /**
     * 构建授权 URL
     *
     * @param state         state 参数
     * @param codeChallenge PKCE code_challenge
     * @param scope         作用域
     * @return 授权 URL 字符串
     */
    private String buildAuthorizationUrl(String state, String codeChallenge, String scope) {
        return UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                .path(properties.getEndpoints().getAuthUri())
                .queryParam("response_type", "code")
                .queryParam("client_id", properties.getClientId())
                .queryParam("redirect_uri", properties.getRedirectUri())
                .queryParam("scope", scope)
                .queryParam("state", state)
                .queryParam("code_challenge", codeChallenge)
                .queryParam("code_challenge_method", "S256")
                .build()
                .toUriString();
    }

    /**
     * 撤销令牌
     *
     * @param token     要撤销的令牌
     * @param tokenType 令牌类型
     * @return 如果撤销成功返回 {@code true}，否则返回 {@code false}
     */
    public Mono<Boolean> revokeToken(String token, String tokenType) {
        return Mono.defer(() -> {
            if (!StringUtils.hasText(token)) {
                log.warn("[HTTP] 待撤销的令牌为空");
                return Mono.just(false);
            }

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("token", token);
            formData.add("token_type_hint", tokenType);
            formData.add("client_id", properties.getClientId());

            if (StringUtils.hasText(properties.getClientSecret())) {
                formData.add("client_secret", properties.getClientSecret());
            }

            String revokeUrl = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                    .path(properties.getEndpoints().getRevocationUri())
                    .build()
                    .toUriString();

            log.debug("[HTTP] 正在撤销令牌: {}", revokeUrl);

            return ssoWebClient
                    .post()
                    .uri(revokeUrl)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(response -> {
                        log.debug("[HTTP] 令牌撤销成功");
                        return true;
                    })
                    .onErrorResume(error -> {
                        log.warn("[HTTP] 撤销令牌失败: {}", error.getMessage());
                        return Mono.just(false);
                    });
        });
    }

    // ========== SDK Request/Result 方法 ==========

    /**
     * 生成授权 URL（SDK Request/Result）
     *
     * @param request 授权 URL 请求
     * @return 授权 URL 结果
     */
    public AuthorizationUrlResult generateAuthorizationUrlSdk(AuthorizationUrlRequest request) {
        String codeVerifier = PkceUtil.generateCodeVerifier();
        String codeChallenge = PkceUtil.generateCodeChallenge(codeVerifier);
        String scope = StringUtils.hasText(request.getScope()) ? request.getScope() : "openid profile email phone";
        String url = buildAuthorizationUrl(request.getState(), codeChallenge, scope);
        return AuthorizationUrlResult.builder()
                .url(url)
                .state(request.getState())
                .codeChallenge(codeChallenge)
                .codeVerifier(codeVerifier)
                .build();
    }

    /**
     * 使用授权码交换令牌（SDK Request/Result）
     *
     * @param request 授权码交换请求
     * @return 令牌结果
     */
    public Mono<TokenResult> exchangeCodeForTokenSdk(ExchangeCodeRequest request) {
        return Mono.defer(() -> {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "authorization_code");
            formData.add("code", request.getCode());
            formData.add("redirect_uri", request.getRedirectUri());
            formData.add("client_id", properties.getClientId());
            formData.add("code_verifier", request.getCodeVerifier());

            if (StringUtils.hasText(properties.getClientSecret())) {
                formData.add("client_secret", properties.getClientSecret());
            }

            String tokenUrl = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                    .path(properties.getEndpoints().getTokenUri())
                    .build()
                    .toUriString();

            log.debug("[HTTP] 正在用授权码交换令牌: {}", tokenUrl);

            return ssoWebClient
                    .post()
                    .uri(tokenUrl)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(TokenResponseDto.class)
                    .map(dto -> TokenResult.builder()
                            .accessToken(dto.accessToken)
                            .tokenType(dto.tokenType != null ? dto.tokenType : "Bearer")
                            .expiresIn(dto.expiresIn)
                            .refreshToken(dto.refreshToken)
                            .scope(dto.scope)
                            .createdAt(System.currentTimeMillis())
                            .build())
                    .onErrorMap(error -> {
                        log.error("[HTTP] 授权码交换失败: {}", error.getMessage());
                        if (error instanceof TokenException) {
                            return error;
                        }
                        return new TokenException(
                                SsoErrorCode.INVALID_CODE,
                                "授权码交换失败: " + error.getMessage(),
                                error,
                                TokenException.TOKEN_TYPE_AUTHORIZATION_CODE
                        );
                    });
        });
    }

    /**
     * 刷新令牌（SDK Request/Result）
     *
     * @param request 刷新令牌请求
     * @return 令牌结果
     */
    public Mono<TokenResult> refreshTokenSdk(RefreshTokenRequest request) {
        return Mono.defer(() -> {
            if (!StringUtils.hasText(request.getRefreshToken())) {
                return Mono.error(new TokenException(
                        TokenException.TOKEN_TYPE_REFRESH,
                        "刷新令牌不能为空"
                ));
            }

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "refresh_token");
            formData.add("refresh_token", request.getRefreshToken());
            formData.add("client_id", properties.getClientId());

            if (StringUtils.hasText(properties.getClientSecret())) {
                formData.add("client_secret", properties.getClientSecret());
            }

            String tokenUrl = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                    .path(properties.getEndpoints().getTokenUri())
                    .build()
                    .toUriString();

            log.debug("[HTTP] 正在刷新令牌: {}", tokenUrl);

            return ssoWebClient
                    .post()
                    .uri(tokenUrl)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(TokenResponseDto.class)
                    .map(dto -> TokenResult.builder()
                            .accessToken(dto.accessToken)
                            .tokenType(dto.tokenType != null ? dto.tokenType : "Bearer")
                            .expiresIn(dto.expiresIn)
                            .refreshToken(dto.refreshToken)
                            .scope(dto.scope)
                            .createdAt(System.currentTimeMillis())
                            .build())
                    .onErrorMap(error -> {
                        log.error("[HTTP] 刷新令牌失败: {}", error.getMessage());
                        if (error instanceof TokenException) {
                            return error;
                        }
                        return new TokenException(
                                SsoErrorCode.TOKEN_INVALID,
                                "刷新令牌失败: " + error.getMessage(),
                                error,
                                TokenException.TOKEN_TYPE_REFRESH
                        );
                    });
        });
    }

    /**
     * 撤销令牌（SDK Request）
     *
     * @param request 撤销令牌请求
     * @return 如果撤销成功返回 {@code true}，否则返回 {@code false}
     */
    public Mono<Boolean> revokeTokenSdk(RevokeTokenRequest request) {
        return revokeToken(request.getToken(), request.getTokenType());
    }

    /**
     * 令牌自省（SDK Request/Result）
     *
     * @param request 令牌自省请求
     * @return 令牌自省结果
     */
    public Mono<IntrospectResult> introspectTokenSdk(IntrospectTokenRequest request) {
        return Mono.defer(() -> {
            if (!StringUtils.hasText(request.getToken())) {
                return Mono.error(new TokenException(
                        "用于内省的令牌不能为空"
                ));
            }

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("token", request.getToken());
            formData.add("token_type_hint", request.getTokenType());
            formData.add("client_id", properties.getClientId());

            if (StringUtils.hasText(properties.getClientSecret())) {
                formData.add("client_secret", properties.getClientSecret());
            }

            String introspectUrl = UriComponentsBuilder.fromUriString(properties.getBaseUrl())
                    .path(properties.getEndpoints().getIntrospectionUri())
                    .build()
                    .toUriString();

            log.debug("[HTTP] 正在内省令牌: {}", introspectUrl);

            return ssoWebClient
                    .post()
                    .uri(introspectUrl)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(IntrospectResponseDto.class)
                    .map(dto -> IntrospectResult.builder()
                            .active(dto.active)
                            .scope(dto.scope)
                            .clientId(dto.clientId)
                            .username(dto.username)
                            .tokenType(dto.tokenType)
                            .exp(dto.exp)
                            .iat(dto.iat)
                            .nbf(dto.nbf)
                            .sub(dto.sub)
                            .aud(dto.aud)
                            .iss(dto.iss)
                            .jti(dto.jti)
                            .build())
                    .onErrorMap(error -> {
                        log.error("[HTTP] 内省令牌失败: {}", error.getMessage());
                        if (error instanceof TokenException tokenException) {
                            return tokenException;
                        }
                        return new TokenException(
                                SsoErrorCode.INTROSPECTION_FAILED,
                                "内省令牌失败: " + error.getMessage(),
                                error,
                                null
                        );
                    });
        });
    }

    /**
     * 验证令牌有效性（SDK Request/Result）
     *
     * @param request 令牌验证请求
     * @return 令牌验证结果
     */
    public Mono<ValidateResult> validateTokenSdk(ValidateTokenRequest request) {
        if (request.getToken() == null || request.getTokenType() == null) {
            return Mono.just(ValidateResult.builder()
                    .valid(false)
                    .message("令牌或令牌类型为空")
                    .build());
        }
        return this.introspectTokenSdk(IntrospectTokenRequest.builder()
                        .token(request.getToken())
                        .tokenType(request.getTokenType())
                        .build())
                .map(introspect -> ValidateResult.builder()
                        .valid(introspect.getActive() != null && introspect.getActive())
                        .message(introspect.getActive() != null && introspect.getActive() ? "令牌有效" : "令牌无效")
                        .build())
                .onErrorResume(error -> {
                    log.warn("[HTTP] 令牌验证失败: {}", error.getMessage());
                    return Mono.just(ValidateResult.builder()
                            .valid(false)
                            .message("令牌验证失败: " + error.getMessage())
                            .build());
                });
    }

    // ========== 内部 DTO 类 ==========

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TokenResponseDto {
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("token_type")
        private String tokenType;
        @JsonProperty("expires_in")
        private Long expiresIn;
        @JsonProperty("refresh_token")
        private String refreshToken;
        @JsonProperty("scope")
        private String scope;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class IntrospectResponseDto {
        private Boolean active;
        private String scope;
        @JsonProperty("client_id")
        private String clientId;
        private String username;
        @JsonProperty("token_type")
        private String tokenType;
        private Long exp;
        private Long iat;
        private Long nbf;
        private String sub;
        private String aud;
        private String iss;
        private String jti;
    }
}
