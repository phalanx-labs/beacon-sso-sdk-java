package com.frontleaves.phalanx.beacon.sso.sdk.springboot.logic;

import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoOAuthApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.OAuthStateException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoConfigurationException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.TokenException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.entity.OAuthState;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.account.RevokeTokenRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.oauth.AuthorizationUrlRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.oauth.ExchangeCodeRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.oauth.RefreshTokenRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.oauth.ValidateTokenRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.oauth.AuthorizationUrlResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.oauth.TokenResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.oauth.ValidateResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.PkceUtil;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.repository.OAuthStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

/**
 * OAuth 授权码流程逻辑
 * <p>
 * 提供 OAuth 2.0 授权码流程的完整实现，支持 PKCE (Proof Key for Code Exchange) 扩展。
 * 包含授权 URL 生成（含 State/PKCE 管理）、回调处理、令牌刷新和撤销等功能。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RequiredArgsConstructor
public class AuthLogic {

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
    private final SsoOAuthApi ssoOAuthApi;
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
        return generateAuthorizationUrl(null);
    }

    /**
     * 生成授权 URL（含 PKCE 和自定义作用域）
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
            log.debug("已生成 OAuth state: {}", maskState(state));

            // 通过 SsoOAuthApi 构建授权 URL
            AuthorizationUrlRequest request = AuthorizationUrlRequest.builder()
                    .state(state)
                    .codeChallenge(codeChallenge)
                    .scope(scope)
                    .build();
            AuthorizationUrlResult result = ssoOAuthApi.generateAuthorizationUrl(request);
            return result.getUrl();
        });
    }

    /**
     * 处理回调，验证 state，获取 token
     *
     * @param code  授权码
     * @param state 状态参数
     * @return TokenResult 令牌响应
     */
    public Mono<TokenResult> handleCallback(String code, String state) {
        return Mono.defer(() -> {
            // 验证参数
            if (!StringUtils.hasText(code)) {
                return Mono.error(new TokenException(
                        SsoErrorCode.INVALID_CODE,
                        "授权码不能为空"
                ));
            }
            if (!StringUtils.hasText(state)) {
                return Mono.error(new OAuthStateException(
                        SsoErrorCode.INVALID_STATE,
                        "State 参数不能为空"
                ));
            }

            // 获取并验证存储的 state
            return Mono.justOrEmpty(stateRepository.findByState(state))
                    .switchIfEmpty(Mono.error(new OAuthStateException(
                            state,
                            "在存储库中未找到 OAuth state"
                    )))
                    .flatMap(oauthState -> {
                        // 检查 state 是否匹配
                        if (!state.equals(oauthState.getState())) {
                            return Mono.error(new OAuthStateException(
                                    state,
                                    "State 参数不匹配"
                            ));
                        }

                        // 检查 state 是否过期
                        if (oauthState.isExpired()) {
                            stateRepository.delete(state);
                            return Mono.error(new OAuthStateException(
                                    state,
                                    "OAuth state 已过期"
                            ));
                        }

                        // 使用授权码交换令牌
                        ExchangeCodeRequest request = ExchangeCodeRequest.builder()
                                .code(code)
                                .redirectUri(oauthState.getRedirectUri())
                                .codeVerifier(oauthState.getCodeVerifier())
                                .build();
                        return ssoOAuthApi.exchangeCodeForToken(request)
                                .doOnSuccess(token -> {
                                    // 成功后删除已使用的 state
                                    stateRepository.delete(state);
                                    log.debug("OAuth state 已使用并删除: {}", maskState(state));
                                })
                                .doOnError(error -> {
                                    // 发生错误时也删除 state
                                    stateRepository.delete(state);
                                    log.warn("OAuth 令牌交换失败，state 已删除: {}", maskState(state));
                                });
                    });
        });
    }

    /**
     * 刷新 token
     *
     * @param refreshToken 刷新令牌
     * @return 新的 TokenResult 令牌响应
     */
    public Mono<TokenResult> refreshToken(String refreshToken) {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken(refreshToken)
                .build();
        return ssoOAuthApi.refreshToken(request);
    }

    /**
     * 撤销 token
     *
     * @param token 要撤销的令牌
     * @return 如果撤销成功返回 {@code true}，否则返回 {@code false}
     */
    public Mono<Boolean> revokeToken(String token) {
        return revokeToken(token, "access_token");
    }

    /**
     * 撤销 token（指定令牌类型）
     *
     * @param token     要撤销的令牌
     * @param tokenType 令牌类型（access_token 或 refresh_token）
     * @return 如果撤销成功返回 {@code true}，否则返回 {@code false}
     */
    public Mono<Boolean> revokeToken(String token, String tokenType) {
        RevokeTokenRequest request = RevokeTokenRequest.builder()
                .token(token)
                .tokenTypeHint(tokenType)
                .build();
        return ssoOAuthApi.revokeToken(request);
    }

    /**
     * 验证 token
     *
     * @param token 要验证的令牌
     * @return ValidateResult 验证结果
     */
    public Mono<ValidateResult> validateToken(String token) {
        ValidateTokenRequest request = ValidateTokenRequest.builder()
                .token(token)
                .build();
        return ssoOAuthApi.validateToken(request);
    }

    /**
     * 生成随机 state 参数
     */
    private String generateState() {
        byte[] randomBytes = new byte[16];
        SECURE_RANDOM.nextBytes(randomBytes);
        return BASE64_URL_ENCODER.encodeToString(randomBytes) + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 验证 SSO 配置是否有效
     */
    private void validateConfiguration() {
        if (!StringUtils.hasText(properties.getBaseUrl())) {
            throw new SsoConfigurationException("SSO 基础 URL 未配置");
        }
        if (!StringUtils.hasText(properties.getClientId())) {
            throw new SsoConfigurationException("SSO 客户端 ID 未配置");
        }
        if (!StringUtils.hasText(properties.getRedirectUri())) {
            throw new SsoConfigurationException("SSO 回调地址未配置");
        }
    }

    /**
     * 对 State 进行脱敏处理，仅显示前8位
     */
    private String maskState(String state) {
        if (state == null || state.length() <= 8) {
            return "****";
        }
        return state.substring(0, 8) + "...";
    }
}
