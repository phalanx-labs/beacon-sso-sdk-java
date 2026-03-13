package com.phalanx.beacon.sso.sdk.base.api;

import com.phalanx.beacon.sso.sdk.base.api.base.BaseBusinessApi;
import com.phalanx.beacon.sso.sdk.base.models.OAuthIntrospection;
import com.phalanx.beacon.sso.sdk.base.models.OAuthUserinfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 阻塞式业务 API（适配 Servlet）
 * <p>
 * 提供阻塞式的 SSO 业务功能，包括获取用户信息、令牌自省和令牌验证。
 * 适用于传统的 Servlet 环境。
 * 内部调用响应式的 {@link BaseBusinessApi}，使用 {@code .block()} 转换为阻塞式调用。
 * </p>
 * <p>
 * 注意：此 API 仅适用于非响应式环境，响应式环境请使用 {@link BaseBusinessApi}。
 * </p>
 *
 * @author xiao_lfeng
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessApi {

    private final BaseBusinessApi baseBusinessApi;

    /**
     * 获取用户信息
     * <p>
     * 使用访问令牌从用户信息端点获取已认证用户的详细信息。
     * </p>
     *
     * @param accessToken 访问令牌
     * @return OAuthUserinfo 用户信息
     */
    public OAuthUserinfo getUserinfo(String accessToken) {
        log.debug("[Blocking] Fetching userinfo with access token");
        return baseBusinessApi.getUserinfo(accessToken).block();
    }

    /**
     * 令牌自省
     * <p>
     * 调用令牌自省端点（RFC 7662）获取令牌的详细元数据信息。
     * </p>
     *
     * @param token 要自省的令牌
     * @return OAuthIntrospection 令牌自省结果
     */
    public OAuthIntrospection introspectToken(String token) {
        log.debug("[Blocking] Introspecting token");
        return baseBusinessApi.introspectToken(token).block();
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
    public boolean validateToken(String token) {
        log.debug("[Blocking] Validating token");
        Boolean result = baseBusinessApi.validateToken(token).block();
        return result != null && result;
    }
}
