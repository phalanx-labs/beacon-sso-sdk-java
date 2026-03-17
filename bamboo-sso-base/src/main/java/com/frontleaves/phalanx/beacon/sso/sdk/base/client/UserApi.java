package com.frontleaves.phalanx.beacon.sso.sdk.base.client;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.TokenException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthIntrospection;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthUserinfo;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
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
 * HTTP User 客户端（纯请求调度）
 * <p>
 * 封装用户信息获取、令牌自省和令牌验证的 HTTP 请求。
 * 不包含缓存逻辑，缓存由上层 Logic 层管理。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RequiredArgsConstructor
public class UserApi {

    private final BeaconSsoProperties properties;
    private final SsoClient ssoClient;

    /**
     * 获取用户信息
     *
     * @param accessToken 访问令牌
     * @return OAuthUserinfo 用户信息
     */
    public Mono<OAuthUserinfo> getUserinfo(String accessToken) {
        return Mono.defer(() -> {
            if (!StringUtils.hasText(accessToken)) {
                return Mono.error(new TokenException(
                        TokenException.TOKEN_TYPE_ACCESS,
                        "Access Token 不能为空"
                ));
            }

            // 构建用户信息端点 URL
            String userinfoUrl = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                    .path(properties.getEndpoints().getUserinfoUri())
                    .build()
                    .toUriString();

            log.debug("正在从以下地址获取用户信息: {}", userinfoUrl);

            WebClient webClient = ssoClient.getUserinfoWebClient();

            return webClient
                    .get()
                    .uri(userinfoUrl)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Accept", "application/json")
                    .retrieve()
                    .bodyToMono(OAuthUserinfo.class)
                    .onErrorMap(error -> {
                        log.error("获取用户信息失败: {}", error.getMessage());
                        if (error instanceof TokenException) {
                            return error;
                        }
                        return new TokenException(
                                SsoErrorCode.USERINFO_FAILED,
                                "获取用户信息失败: " + error.getMessage(),
                                error,
                                TokenException.TOKEN_TYPE_ACCESS
                        );
                    });
        });
    }

    /**
     * 令牌自省
     *
     * @param token 要自省的令牌
     * @return OAuthIntrospection 令牌自省结果
     */
    public Mono<OAuthIntrospection> introspectToken(String token) {
        return introspectToken(token, "access_token");
    }

    /**
     * 令牌自省（指定令牌类型）
     *
     * @param token     要自省的令牌
     * @param tokenType 令牌类型提示
     * @return OAuthIntrospection 令牌自省结果
     */
    public Mono<OAuthIntrospection> introspectToken(String token, String tokenType) {
        return Mono.defer(() -> {
            if (!StringUtils.hasText(token)) {
                return Mono.error(new TokenException(
                        "用于内省的令牌不能为空"
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

            log.debug("正在内省令牌: {}", introspectUrl);

            WebClient webClient = ssoClient.getOAuthWebClient();

            return webClient
                    .post()
                    .uri(introspectUrl)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(OAuthIntrospection.class)
                    .onErrorMap(error -> {
                        log.error("内省令牌失败: {}", error.getMessage());
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
     * 验证令牌有效性
     *
     * @param token 要验证的令牌
     * @return 如果令牌有效返回 {@code true}，否则返回 {@code false}
     */
    public Mono<Boolean> validateToken(String token) {
        return validateToken(token, "access_token");
    }

    /**
     * 验证令牌有效性（指定令牌类型）
     *
     * @param token     要验证的令牌
     * @param tokenType 令牌类型提示
     * @return 如果令牌有效返回 {@code true}，否则返回 {@code false}
     */
    public Mono<Boolean> validateToken(String token, String tokenType) {
        return this.introspectToken(token, tokenType)
                .map(OAuthIntrospection::isActive)
                .onErrorResume(error -> {
                    log.warn("令牌验证失败: {}", error.getMessage());
                    return Mono.just(false);
                });
    }
}
