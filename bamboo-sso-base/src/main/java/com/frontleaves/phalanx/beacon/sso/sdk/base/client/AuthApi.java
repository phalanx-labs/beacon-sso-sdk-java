package com.frontleaves.phalanx.beacon.sso.sdk.base.client;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoConfigurationException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.TokenException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthState;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthToken;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.PkceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

/**
 * HTTP OAuth 客户端（纯请求调度）
 * <p>
 * 封装 OAuth 2.0 协议相关的 HTTP 请求，包括授权 URL 生成、授权码交换、
 * 令牌刷新和撤销。不包含状态管理和缓存逻辑。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RequiredArgsConstructor
public class AuthApi {

    private final BeaconSsoProperties properties;
    private final SsoClient ssoClient;

    /**
     * 生成授权 URL（默认 scope）
     * <p>
     * 纯计算操作，不发起 HTTP 请求。
     * State 和 PKCE 参数由调用方（如 SpringBoot AuthLogic）生成并管理。
     * </p>
     *
     * @param state         state 参数
     * @param codeChallenge PKCE code_challenge
     * @return 授权 URL 字符串
     */
    public String generateAuthorizationUrl(String state, String codeChallenge) {
        return buildAuthorizationUrl(state, codeChallenge, "openid profile email");
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
                StringUtils.hasText(scope) ? scope : "openid profile email");
    }

    /**
     * 使用授权码交换令牌
     *
     * @param code       授权码
     * @param oauthState 已验证的 OAuth State（含 codeVerifier 和 redirectUri）
     * @return OAuthToken 令牌响应
     */
    public Mono<OAuthToken> exchangeCodeForToken(String code, OAuthState oauthState) {
        return Mono.defer(() -> {
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

            log.debug("正在用授权码交换令牌: {}", tokenUrl);

            WebClient webClient = ssoClient.getOAuthWebClient();

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
                        log.error("授权码交换失败: {}", error.getMessage());
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
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的 OAuthToken 令牌响应
     */
    public Mono<OAuthToken> refreshToken(String refreshToken) {
        return Mono.defer(() -> {
            if (!StringUtils.hasText(refreshToken)) {
                return Mono.error(new TokenException(
                        TokenException.TOKEN_TYPE_REFRESH,
                        "刷新令牌不能为空"
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

            log.debug("正在刷新令牌: {}", tokenUrl);

            WebClient webClient = ssoClient.getOAuthWebClient();

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
                        log.error("刷新令牌失败: {}", error.getMessage());
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
     * 撤销令牌
     *
     * @param token     要撤销的令牌
     * @param tokenType 令牌类型（access_token 或 refresh_token）
     * @return 如果撤销成功返回 {@code true}，否则返回 {@code false}
     */
    public Mono<Boolean> revokeToken(String token, String tokenType) {
        return Mono.defer(() -> {
            if (!StringUtils.hasText(token)) {
                log.warn("待撤销的令牌为空");
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

            log.debug("正在撤销令牌: {}", revokeUrl);

            WebClient webClient = ssoClient.getOAuthWebClient();

            // 发送令牌撤销请求
            return webClient
                    .post()
                    .uri(revokeUrl)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(response -> {
                        log.debug("令牌撤销成功");
                        return true;
                    })
                    .onErrorResume(error -> {
                        log.warn("撤销令牌失败: {}", error.getMessage());
                        return Mono.just(false);
                    });
        });
    }

    /**
     * 构建授权 URL
     */
    private String buildAuthorizationUrl(String state, String codeChallenge, String scope) {
        return UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                .path(properties.getEndpoints().getAuthUri())
                .queryParam("response_type", "code")
                .queryParam("client_id", properties.getClientId())
                .queryParam("redirect_uri", properties.getRedirectUri())
                .queryParam("state", state)
                .queryParam("code_challenge", codeChallenge)
                .queryParam("code_challenge_method", PkceUtil.getCodeChallengeMethod())
                .queryParam("scope", scope)
                .build()
                .toUriString();
    }
}
