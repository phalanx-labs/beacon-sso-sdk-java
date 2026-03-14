package com.frontleaves.phalanx.beacon.sso.sdk.base.logic;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoConfigurationException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.TokenException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthIntrospection;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthUserinfo;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
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

/**
 * 业务逻辑（响应式）
 * <p>
 * 提供 SSO 相关的业务功能，包括获取用户信息、令牌自省和令牌验证。
 * 使用已配置的 WebClient 进行响应式 HTTP 调用。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1-SNAPSHOT
 */
@Slf4j
@Service
public class BusinessLogic {

    private final BeaconSsoProperties properties;
    private final WebClient userinfoWebClient;
    private final WebClient oauthWebClient;

    /**
     * 构造函数
     *
     * @param properties        SSO 配置属性
     * @param userinfoWebClient 用户信息专用 WebClient
     * @param oauthWebClient    OAuth 专用 WebClient
     */
    public BusinessLogic(
            BeaconSsoProperties properties,
            @Qualifier("beaconSsoUserinfoWebClient") WebClient userinfoWebClient,
            @Qualifier("beaconSsoOAuthWebClient") WebClient oauthWebClient
    ) {
        this.properties = properties;
        this.userinfoWebClient = userinfoWebClient;
        this.oauthWebClient = oauthWebClient;
        // 初始化时验证配置
        validateConfiguration();
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
    }

    /**
     * 获取用户信息
     * <p>
     * 使用访问令牌从用户信息端点获取已认证用户的详细信息。
     * </p>
     *
     * @param accessToken 访问令牌
     * @return OAuthUserinfo 用户信息
     * @throws TokenException 如果令牌无效或获取用户信息失败
     */
    public Mono<OAuthUserinfo> getUserinfo(String accessToken) {
        return Mono.defer(() -> {
            // 验证参数
            if (!StringUtils.hasText(accessToken)) {
                return Mono.error(new TokenException(
                        TokenException.TOKEN_TYPE_ACCESS,
                        "Access token cannot be null or empty"
                ));
            }

            // 构建用户信息端点 URL
            String userinfoUrl = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                    .path(properties.getEndpoints().getUserinfoUri())
                    .build()
                    .toUriString();

            log.debug("Fetching userinfo from: {}", userinfoUrl);

            // 发送用户信息请求
            return userinfoWebClient
                    .get()
                    .uri(userinfoUrl)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Accept", "application/json")
                    .retrieve()
                    .bodyToMono(OAuthUserinfo.class)
                    .onErrorMap(error -> {
                        log.error("Failed to fetch userinfo: {}", error.getMessage());
                        if (error instanceof TokenException) {
                            return error;
                        }
                        return new TokenException(
                                SsoErrorCode.USERINFO_FAILED,
                                "Failed to fetch userinfo: " + error.getMessage(),
                                error,
                                TokenException.TOKEN_TYPE_ACCESS
                        );
                    });
        });
    }

    /**
     * 令牌自省
     * <p>
     * 调用令牌自省端点（RFC 7662）获取令牌的详细元数据信息。
     * </p>
     *
     * @param token 要自省的令牌
     * @return OAuthIntrospection 令牌自省结果
     * @throws TokenException 如果令牌自省失败
     */
    public Mono<OAuthIntrospection> introspectToken(String token) {
        return introspectToken(token, "access_token");
    }

    /**
     * 令牌自省（指定令牌类型）
     * <p>
     * 调用令牌自省端点（RFC 7662）获取令牌的详细元数据信息。
     * </p>
     *
     * @param token     要自省的令牌
     * @param tokenType 令牌类型提示（access_token 或 refresh_token）
     * @return OAuthIntrospection 令牌自省结果
     * @throws TokenException 如果令牌自省失败
     */
    public Mono<OAuthIntrospection> introspectToken(String token, String tokenType) {
        return Mono.defer(() -> {
            // 验证参数
            if (!StringUtils.hasText(token)) {
                return Mono.error(new TokenException(
                        "Token cannot be null or empty for introspection"
                ));
            }

            // 构建请求参数
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("token", token);
            formData.add("token_type_hint", tokenType);
            formData.add("client_id", properties.getClientId());

            if (StringUtils.hasText(properties.getClientSecret())) {
                formData.add("client_secret", properties.getClientSecret());
            }

            // 构建自省端点 URL
            String introspectUrl = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                    .path(properties.getEndpoints().getIntrospectionUri())
                    .build()
                    .toUriString();

            log.debug("Introspecting token at: {}", introspectUrl);

            // 发送令牌自省请求
            return oauthWebClient
                    .post()
                    .uri(introspectUrl)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(OAuthIntrospection.class)
                    .onErrorMap(error -> {
                        log.error("Failed to introspect token: {}", error.getMessage());
                        if (error instanceof TokenException tokenException) {
                            return tokenException;
                        }
                        return new TokenException(
                                SsoErrorCode.INTROSPECTION_FAILED,
                                "Failed to introspect token: " + error.getMessage(),
                                error,
                                null
                        );
                    });
        });
    }

    /**
     * 验证令牌有效性
     * <p>
     * 通过令牌自省端点验证令牌是否有效且处于活跃状态。
     * </p>
     *
     * @param token 要验证的令牌
     * @return 如果令牌有效返回 {@code true}，否则返回 {@code false}
     */
    public Mono<Boolean> validateToken(String token) {
        return validateToken(token, "access_token");
    }

    /**
     * 验证令牌有效性（指定令牌类型）
     * <p>
     * 通过令牌自省端点验证令牌是否有效且处于活跃状态。
     * </p>
     *
     * @param token     要验证的令牌
     * @param tokenType 令牌类型提示（access_token 或 refresh_token）
     * @return 如果令牌有效返回 {@code true}，否则返回 {@code false}
     */
    public Mono<Boolean> validateToken(String token, String tokenType) {
        return this.introspectToken(token, tokenType)
                .map(OAuthIntrospection::isActive)
                .onErrorResume(error -> {
                    log.warn("Token validation failed: {}", error.getMessage());
                    return Mono.just(false);
                });
    }

    /**
     * 验证令牌并获取自省信息
     * <p>
     * 验证令牌有效性，如果有效则返回自省信息，否则返回空。
     * </p>
     *
     * @param token 要验证的令牌
     * @return 如果令牌有效返回自省信息，否则返回空
     */
    public Mono<OAuthIntrospection> validateAndGetIntrospection(String token) {
        return this.introspectToken(token)
                .filter(OAuthIntrospection::isActive)
                .onErrorResume(error -> {
                    log.debug("Token validation returned empty: {}", error.getMessage());
                    return Mono.empty();
                });
    }

    /**
     * 检查令牌是否即将过期
     * <p>
     * 检查令牌是否在指定秒数内即将过期。
     * </p>
     *
     * @param token           要检查的令牌
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
                    log.warn("Failed to check token expiration: {}", error.getMessage());
                    return Mono.just(true);
                });
    }

    /**
     * 获取令牌剩余有效时间
     * <p>
     * 计算令牌的剩余有效时间（秒）。
     * </p>
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
                    log.warn("Failed to get token remaining time: {}", error.getMessage());
                    return Mono.just(0L);
                });
    }
}
