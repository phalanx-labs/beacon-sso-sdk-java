package com.frontleaves.phalanx.beacon.sso.sdk.base.api;

import com.frontleaves.phalanx.beacon.sso.sdk.base.api.base.BaseOAuthApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 阻塞式 OAuth API（适配 Servlet）
 * <p>
 * 提供阻塞式的 OAuth 2.0 API 接口，适用于传统的 Servlet 环境。
 * 内部调用响应式的 {@link BaseOAuthApi}，使用 {@code .block()} 转换为阻塞式调用。
 * </p>
 * <p>
 * 注意：此 API 仅适用于非响应式环境，响应式环境请使用 {@link BaseOAuthApi}。
 * </p>
 *
 * @author xiao_lfeng
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthApi {

    private final BaseOAuthApi baseOAuthApi;

    /**
     * 生成授权 URL（含 PKCE）
     * <p>
     * 构建 OAuth 2.0 授权码流程的授权 URL，自动生成 state 参数和 PKCE 相关参数。
     * </p>
     *
     * @return 授权 URL 字符串
     */
    public String generateAuthorizationUrl() {
        log.debug("[Blocking] Generating OAuth authorization URL");
        return baseOAuthApi.generateAuthorizationUrl().block();
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
     */
    public OAuthToken handleCallback(String code, String state) {
        log.debug("[Blocking] Handling OAuth callback with code and state");
        return baseOAuthApi.handleCallback(code, state).block();
    }

    /**
     * 刷新 token
     * <p>
     * 使用刷新令牌获取新的访问令牌。
     * </p>
     *
     * @param refreshToken 刷新令牌
     * @return 新的 OAuthToken 令牌响应
     */
    public OAuthToken refreshToken(String refreshToken) {
        log.debug("[Blocking] Refreshing OAuth token");
        return baseOAuthApi.refreshToken(refreshToken).block();
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
    public boolean revokeToken(String token) {
        log.debug("[Blocking] Revoking OAuth token");
        Boolean result = baseOAuthApi.revokeToken(token).block();
        return result != null && result;
    }
}
