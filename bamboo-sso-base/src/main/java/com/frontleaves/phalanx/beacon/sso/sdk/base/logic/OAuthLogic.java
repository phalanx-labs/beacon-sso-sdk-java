package com.frontleaves.phalanx.beacon.sso.sdk.base.logic;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.OAuthStateException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoConfigurationException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.TokenException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthState;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthToken;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.base.repository.OAuthStateRepository;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.PkceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

/**
 * OAuth 授权码流程逻辑（响应式）
 * <p>
 * 提供 OAuth 2.0 授权码流程的完整实现，支持 PKCE (Proof Key for Code Exchange) 扩展。
 * 包含授权 URL 生成、回调处理、令牌刷新和撤销等功能。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1-SNAPSHOT
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthLogic {

    /**
     * State 默认过期时间（毫秒）: 5 分钟
     */
    private static final long STATE_EXPIRATION_TIME = 5 * 60 * 1000L;

    /**
     * 安全随机数生成器
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Base64 URL 编码器（无填充）
     */
    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

    private final BeaconSsoProperties properties;
    @Qualifier("beaconSsoOAuthWebClient")
    private final WebClient webClient;
    private final OAuthStateRepository stateRepository;

    /**
     * 生成授权 URL（含 PKCE）
     * <p>
     * 构建 OAuth 2.0 授权码流程的授权 URL，自动生成 state 参数和 PKCE 相关参数。
     * State 信息会被存储到 OAuthStateRepository 中，用于后续回调验证。
     * </p>
     *
     * @return 授权 URL 字符串
     * @throws SsoConfigurationException 如果 SSO 配置无效
     */
    public Mono<String> generateAuthorizationUrl() {
        return Mono.fromCallable(() -> {
            // 验证配置
            validateConfiguration();

            // 生成 state 和 PKCE 参数
            String state = generateState();
            String codeVerifier = PkceUtil.generateCodeVerifier();
            String codeChallenge = PkceUtil.generateCodeChallenge(codeVerifier);

            // 构建 OAuth State 对象并存储
            OAuthState oauthState = OAuthState.builder()
                    .state(state)
                    .codeVerifier(codeVerifier)
                    .redirectUri(properties.getRedirectUri())
                    .createdAt(System.currentTimeMillis())
                    .expiresAt(System.currentTimeMillis() + STATE_EXPIRATION_TIME)
                    .build();

            stateRepository.save(state, oauthState);
            log.debug("Generated OAuth state: {}", maskState(state));

            // 构建授权 URL
            return UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                    .path(properties.getEndpoints().getAuthUri())
                    .queryParam("response_type", "code")
                    .queryParam("client_id", properties.getClientId())
                    .queryParam("redirect_uri", properties.getRedirectUri())
                    .queryParam("state", state)
                    .queryParam("code_challenge", codeChallenge)
                    .queryParam("code_challenge_method", PkceUtil.getCodeChallengeMethod())
                    .queryParam("scope", "openid profile email")
                    .build()
                    .toUriString();
        });
    }

    /**
     * 生成授权 URL（含 PKCE 和自定义作用域）
     * <p>
     * 构建 OAuth 2.0 授权码流程的授权 URL，支持自定义作用域。
     * </p>
     *
     * @param scope 自定义作用域字符串
     * @return 授权 URL 字符串
     * @throws SsoConfigurationException 如果 SSO 配置无效
     */
    public Mono<String> generateAuthorizationUrl(String scope) {
        return Mono.fromCallable(() -> {
            // 验证配置
            validateConfiguration();

            // 生成 state 和 PKCE 参数
            String state = generateState();
            String codeVerifier = PkceUtil.generateCodeVerifier();
            String codeChallenge = PkceUtil.generateCodeChallenge(codeVerifier);

            // 构建 OAuth State 对象并存储
            OAuthState oauthState = OAuthState.builder()
                    .state(state)
                    .codeVerifier(codeVerifier)
                    .redirectUri(properties.getRedirectUri())
                    .createdAt(System.currentTimeMillis())
                    .expiresAt(System.currentTimeMillis() + STATE_EXPIRATION_TIME)
                    .build();

            stateRepository.save(state, oauthState);
            log.debug("Generated OAuth state: {}", maskState(state));

            // 构建授权 URL
            return UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                    .path(properties.getEndpoints().getAuthUri())
                    .queryParam("response_type", "code")
                    .queryParam("client_id", properties.getClientId())
                    .queryParam("redirect_uri", properties.getRedirectUri())
                    .queryParam("state", state)
                    .queryParam("code_challenge", codeChallenge)
                    .queryParam("code_challenge_method", PkceUtil.getCodeChallengeMethod())
                    .queryParam("scope", StringUtils.hasText(scope) ? scope : "openid profile email")
                    .build()
                    .toUriString();
        });
    }

    /**
     * 处理回调，验证 state，获取 token
     * <p>
     * 处理 OAuth 授权服务器回调，验证 state 参数的有效性，
     * 使用授权码和 PKCE code_verifier 交换访问令牌。
     * </p>
     *
     * @param code  授权码
     * @param state 状态参数
     * @return OAuthToken 令牌响应
     * @throws OAuthStateException 如果 state 无效或已过期
     * @throws TokenException       如果令牌交换失败
     */
    public Mono<OAuthToken> handleCallback(String code, String state) {
        return Mono.defer(() -> {
            // 验证参数
            if (!StringUtils.hasText(code)) {
                return Mono.error(new TokenException(
                        SsoErrorCode.INVALID_CODE,
                        "Authorization code cannot be null or empty"
                ));
            }
            if (!StringUtils.hasText(state)) {
                return Mono.error(new OAuthStateException(
                        SsoErrorCode.INVALID_STATE,
                        "State parameter cannot be null or empty"
                ));
            }

            // 获取并验证存储的 state
            return Mono.justOrEmpty(stateRepository.findByState(state))
                    .switchIfEmpty(Mono.error(new OAuthStateException(
                            state,
                            "OAuth state not found in repository"
                    )))
                    .flatMap(oauthState -> {
                        // 检查 state 是否匹配
                        if (!state.equals(oauthState.getState())) {
                            return Mono.error(new OAuthStateException(
                                    state,
                                    "State parameter mismatch"
                            ));
                        }

                        // 检查 state 是否过期
                        if (oauthState.isExpired()) {
                            stateRepository.delete(state);
                            return Mono.error(new OAuthStateException(
                                    state,
                                    "OAuth state has expired"
                            ));
                        }

                        // 使用授权码交换令牌
                        return exchangeCodeForToken(code, oauthState)
                                .doOnSuccess(token -> {
                                    // 成功后删除已使用的 state
                                    stateRepository.delete(state);
                                    log.debug("OAuth state consumed and deleted: {}", maskState(state));
                                })
                                .doOnError(error -> {
                                    // 发生错误时也删除 state
                                    stateRepository.delete(state);
                                    log.warn("OAuth token exchange failed, state deleted: {}", maskState(state));
                                });
                    });
        });
    }

    /**
     * 刷新 token
     * <p>
     * 使用刷新令牌获取新的访问令牌。
     * </p>
     *
     * @param refreshToken 刷新令牌
     * @return 新的 OAuthToken 令牌响应
     * @throws TokenException 如果刷新令牌无效或刷新失败
     */
    public Mono<OAuthToken> refreshToken(String refreshToken) {
        return Mono.defer(() -> {
            if (!StringUtils.hasText(refreshToken)) {
                return Mono.error(new TokenException(
                        TokenException.TOKEN_TYPE_REFRESH,
                        "Refresh token cannot be null or empty"
                ));
            }

            // 构建请求参数
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "refresh_token");
            formData.add("refresh_token", refreshToken);
            formData.add("client_id", properties.getClientId());

            if (StringUtils.hasText(properties.getClientSecret())) {
                formData.add("client_secret", properties.getClientSecret());
            }

            // 构建令牌端点 URL
            String tokenUrl = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                    .path(properties.getEndpoints().getTokenUri())
                    .build()
                    .toUriString();

            log.debug("Refreshing token at: {}", tokenUrl);

            // 发送令牌刷新请求
            return webClient
                    .post()
                    .uri(tokenUrl)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(OAuthToken.class)
                    .map(token -> {
                        token.setCreatedAt(System.currentTimeMillis());
                        return token;
                    })
                    .onErrorMap(error -> {
                        log.error("Failed to refresh token: {}", error.getMessage());
                        if (error instanceof TokenException) {
                            return error;
                        }
                        return new TokenException(
                                SsoErrorCode.TOKEN_INVALID,
                                "Failed to refresh token: " + error.getMessage(),
                                error,
                                TokenException.TOKEN_TYPE_REFRESH
                        );
                    });
        });
    }

    /**
     * 撤销 token
     * <p>
     * 撤销访问令牌或刷新令牌，使令牌立即失效。
     * </p>
     *
     * @param token 要撤销的令牌
     * @return 如果撤销成功返回 {@code true}，否则返回 {@code false}
     */
    public Mono<Boolean> revokeToken(String token) {
        return revokeToken(token, "access_token");
    }

    /**
     * 撤销 token（指定令牌类型）
     * <p>
     * 撤销访问令牌或刷新令牌，使令牌立即失效。
     * </p>
     *
     * @param token     要撤销的令牌
     * @param tokenType 令牌类型（access_token 或 refresh_token）
     * @return 如果撤销成功返回 {@code true}，否则返回 {@code false}
     */
    public Mono<Boolean> revokeToken(String token, String tokenType) {
        return Mono.defer(() -> {
            if (!StringUtils.hasText(token)) {
                log.warn("Token to revoke is null or empty");
                return Mono.just(false);
            }

            // 构建请求参数
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("token", token);
            formData.add("token_type_hint", tokenType);
            formData.add("client_id", properties.getClientId());

            if (StringUtils.hasText(properties.getClientSecret())) {
                formData.add("client_secret", properties.getClientSecret());
            }

            // 构建撤销端点 URL
            String revokeUrl = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                    .path(properties.getEndpoints().getRevocationUri())
                    .build()
                    .toUriString();

            log.debug("Revoking token at: {}", revokeUrl);

            // 发送令牌撤销请求
            return webClient
                    .post()
                    .uri(revokeUrl)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(response -> {
                        log.debug("Token revoked successfully");
                        return true;
                    })
                    .onErrorResume(error -> {
                        log.warn("Failed to revoke token: {}", error.getMessage());
                        return Mono.just(false);
                    });
        });
    }

    /**
     * 使用授权码交换令牌
     * <p>
     * 向令牌端点发送授权码和 PKCE code_verifier，获取访问令牌。
     * </p>
     *
     * @param code       授权码
     * @param oauthState OAuth State 对象
     * @return OAuthToken 令牌响应
     */
    private Mono<OAuthToken> exchangeCodeForToken(String code, OAuthState oauthState) {
        // 构建请求参数
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("redirect_uri", oauthState.getRedirectUri());
        formData.add("client_id", properties.getClientId());
        formData.add("code_verifier", oauthState.getCodeVerifier());

        if (StringUtils.hasText(properties.getClientSecret())) {
            formData.add("client_secret", properties.getClientSecret());
        }

        // 构建令牌端点 URL
        String tokenUrl = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                .path(properties.getEndpoints().getTokenUri())
                .build()
                .toUriString();

        log.debug("Exchanging authorization code for token at: {}", tokenUrl);

        // 发送令牌请求
        return webClient
                .post()
                .uri(tokenUrl)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(OAuthToken.class)
                .map(token -> {
                    token.setCreatedAt(System.currentTimeMillis());
                    return token;
                })
                .onErrorMap(error -> {
                    log.error("Failed to exchange authorization code: {}", error.getMessage());
                    if (error instanceof TokenException) {
                        return error;
                    }
                    return new TokenException(
                            SsoErrorCode.INVALID_CODE,
                            "Failed to exchange authorization code: " + error.getMessage(),
                            error,
                            TokenException.TOKEN_TYPE_AUTHORIZATION_CODE
                    );
                });
    }

    /**
     * 生成随机 state 参数
     * <p>
     * 生成一个安全的随机字符串作为 OAuth state 参数。
     * </p>
     *
     * @return 随机 state 字符串
     */
    private String generateState() {
        byte[] randomBytes = new byte[16];
        SECURE_RANDOM.nextBytes(randomBytes);
        return BASE64_URL_ENCODER.encodeToString(randomBytes) + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 验证 SSO 配置是否有效
     *
     * @throws SsoConfigurationException 如果配置无效
     */
    private void validateConfiguration() {
        if (!StringUtils.hasText(properties.getBaseUrl())) {
            throw new SsoConfigurationException("SSO base URL is not configured");
        }
        if (!StringUtils.hasText(properties.getClientId())) {
            throw new SsoConfigurationException("SSO client ID is not configured");
        }
        if (!StringUtils.hasText(properties.getRedirectUri())) {
            throw new SsoConfigurationException("SSO redirect URI is not configured");
        }
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
