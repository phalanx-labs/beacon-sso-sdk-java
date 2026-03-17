package com.frontleaves.phalanx.beacon.sso.sdk.base.utility;

import java.io.Serial;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * OAuth URL 构建器
 * <p>
 * 提供 OAuth 2.0/OIDC 流程中各类端点 URL 的构建功能。
 * 支持构建授权 URL、令牌 URL、用户信息 URL、自省 URL 和撤销 URL。
 * </p>
 *
 * @author Xiao Lfeng &lt;xiao_lfeng@icloud.com&gt;
 * @since 1.0.0
 */
public final class OAuthUrlBuilder {

    /**
     * OAuth 授权端点路径
     */
    private static final String AUTHORIZE_PATH = "/authorize";

    /**
     * OAuth 令牌端点路径
     */
    private static final String TOKEN_PATH = "/token";

    /**
     * OIDC 用户信息端点路径
     */
    private static final String USERINFO_PATH = "/userinfo";

    /**
     * OAuth 令牌自省端点路径
     */
    private static final String INTROSPECTION_PATH = "/introspect";

    /**
     * OAuth 令牌撤销端点路径
     */
    private static final String REVOCATION_PATH = "/revoke";

    /**
     * 私有构造函数，防止实例化
     */
    private OAuthUrlBuilder() {
        throw new UnsupportedOperationException("工具类不能被实例化");
    }

    /**
     * 构建 OAuth 2.0 授权 URL
     * <p>
     * 构建用于授权码流程的授权端点 URL，支持 PKCE 扩展。
     * </p>
     *
     * @param baseUrl       基础 URL (如 https://sso.example.com/oauth2)
     * @param clientId      客户端 ID
     * @param redirectUri   重定向 URI
     * @param state         状态参数 (用于 CSRF 防护)
     * @param codeChallenge PKCE code_challenge
     * @param scope         请求的权限范围
     * @return 完整的授权 URL
     * @throws IllegalArgumentException 如果必要参数为空
     */
    public static String buildAuthorizationUrl(
            String baseUrl,
            String clientId,
            String redirectUri,
            String state,
            String codeChallenge,
            String scope
    ) {
        validateRequiredParameter(baseUrl, "baseUrl");
        validateRequiredParameter(clientId, "clientId");
        validateRequiredParameter(redirectUri, "redirectUri");

        Map<String, String> params = new LinkedHashMap<>();
        params.put("response_type", "code");
        params.put("client_id", clientId);
        params.put("redirect_uri", redirectUri);

        // 可选参数
        Optional.ofNullable(state)
                .filter(s -> !s.isEmpty())
                .ifPresent(s -> params.put("state", s));

        Optional.ofNullable(codeChallenge)
                .filter(cc -> !cc.isEmpty())
                .ifPresent(cc -> {
                    params.put("code_challenge", cc);
                    params.put("code_challenge_method", PkceUtil.getCodeChallengeMethod());
                });

        Optional.ofNullable(scope)
                .filter(sc -> !sc.isEmpty())
                .ifPresent(sc -> params.put("scope", sc));

        return buildUrlWithParams(normalizeBaseUrl(baseUrl) + AUTHORIZE_PATH, params);
    }

    /**
     * 构建令牌端点 URL
     *
     * @param baseUrl 基础 URL (如 https://sso.example.com/oauth2)
     * @return 令牌端点 URL
     * @throws IllegalArgumentException 如果 baseUrl 为空
     */
    public static String buildTokenUrl(String baseUrl) {
        validateRequiredParameter(baseUrl, "baseUrl");
        return normalizeBaseUrl(baseUrl) + TOKEN_PATH;
    }

    /**
     * 构建用户信息端点 URL
     *
     * @param baseUrl 基础 URL (如 https://sso.example.com/oauth2)
     * @return 用户信息端点 URL
     * @throws IllegalArgumentException 如果 baseUrl 为空
     */
    public static String buildUserinfoUrl(String baseUrl) {
        validateRequiredParameter(baseUrl, "baseUrl");
        return normalizeBaseUrl(baseUrl) + USERINFO_PATH;
    }

    /**
     * 构建令牌自省端点 URL
     *
     * @param baseUrl 基础 URL (如 https://sso.example.com/oauth2)
     * @return 令牌自省端点 URL
     * @throws IllegalArgumentException 如果 baseUrl 为空
     */
    public static String buildIntrospectionUrl(String baseUrl) {
        validateRequiredParameter(baseUrl, "baseUrl");
        return normalizeBaseUrl(baseUrl) + INTROSPECTION_PATH;
    }

    /**
     * 构建令牌撤销端点 URL
     *
     * @param baseUrl 基础 URL (如 https://sso.example.com/oauth2)
     * @return 令牌撤销端点 URL
     * @throws IllegalArgumentException 如果 baseUrl 为空
     */
    public static String buildRevocationUrl(String baseUrl) {
        validateRequiredParameter(baseUrl, "baseUrl");
        return normalizeBaseUrl(baseUrl) + REVOCATION_PATH;
    }

    /**
     * 构建完整的登出 URL
     * <p>
     * 支持 OIDC RP-Initiated Logout 流程。
     * </p>
     *
     * @param baseUrl          基础 URL (如 https://sso.example.com/oauth2)
     * @param idTokenHint      ID Token 提示 (可选)
     * @param postLogoutRedirectUri 登出后重定向 URI (可选)
     * @param state            状态参数 (可选)
     * @return 完整的登出 URL
     * @throws IllegalArgumentException 如果 baseUrl 为空
     */
    public static String buildLogoutUrl(
            String baseUrl,
            String idTokenHint,
            String postLogoutRedirectUri,
            String state
    ) {
        validateRequiredParameter(baseUrl, "baseUrl");

        Map<String, String> params = new LinkedHashMap<>();

        Optional.ofNullable(idTokenHint)
                .filter(hint -> !hint.isEmpty())
                .ifPresent(hint -> params.put("id_token_hint", hint));

        Optional.ofNullable(postLogoutRedirectUri)
                .filter(uri -> !uri.isEmpty())
                .ifPresent(uri -> params.put("post_logout_redirect_uri", uri));

        Optional.ofNullable(state)
                .filter(s -> !s.isEmpty())
                .ifPresent(s -> params.put("state", s));

        String logoutUrl = normalizeBaseUrl(baseUrl) + "/logout";
        return params.isEmpty() ? logoutUrl : buildUrlWithParams(logoutUrl, params);
    }

    /**
     * 验证必要参数是否为空
     *
     * @param parameter 参数值
     * @param name      参数名称
     * @throws IllegalArgumentException 如果参数为 null 或空
     */
    private static void validateRequiredParameter(String parameter, String name) {
        if (parameter == null || parameter.isEmpty()) {
            throw new IllegalArgumentException(name + " 不能为空");
        }
    }

    /**
     * 规范化基础 URL
     * <p>
     * 移除末尾的斜杠，确保 URL 格式一致。
     * </p>
     *
     * @param baseUrl 原始基础 URL
     * @return 规范化后的基础 URL
     */
    private static String normalizeBaseUrl(String baseUrl) {
        return Optional.ofNullable(baseUrl)
                .map(url -> url.endsWith("/") ? url.substring(0, url.length() - 1) : url)
                .orElse(baseUrl);
    }

    /**
     * 构建 URL 并附加查询参数
     *
     * @param url    基础 URL
     * @param params 查询参数 Map
     * @return 带查询参数的完整 URL
     */
    private static String buildUrlWithParams(String url, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(url);
        builder.append("?");

        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) {
                builder.append("&");
            }
            first = false;

            builder.append(urlEncode(entry.getKey()));
            builder.append("=");
            builder.append(urlEncode(entry.getValue()));
        }

        return builder.toString();
    }

    /**
     * URL 编码
     *
     * @param value 待编码的值
     * @return 编码后的值
     */
    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /**
     * OAuth URL 构建器 (Builder 模式)
     * <p>
     * 提供链式调用方式构建 OAuth 授权 URL。
     * </p>
     */
    public static class Builder implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String baseUrl;
        private String clientId;
        private String redirectUri;
        private String state;
        private String codeChallenge;
        private String scope;

        /**
         * 设置基础 URL
         *
         * @param baseUrl 基础 URL
         * @return Builder 实例
         */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * 设置客户端 ID
         *
         * @param clientId 客户端 ID
         * @return Builder 实例
         */
        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        /**
         * 设置重定向 URI
         *
         * @param redirectUri 重定向 URI
         * @return Builder 实例
         */
        public Builder redirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        /**
         * 设置状态参数
         *
         * @param state 状态参数
         * @return Builder 实例
         */
        public Builder state(String state) {
            this.state = state;
            return this;
        }

        /**
         * 设置 PKCE code_challenge
         *
         * @param codeChallenge code_challenge
         * @return Builder 实例
         */
        public Builder codeChallenge(String codeChallenge) {
            this.codeChallenge = codeChallenge;
            return this;
        }

        /**
         * 设置权限范围
         *
         * @param scope 权限范围
         * @return Builder 实例
         */
        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        /**
         * 构建授权 URL
         *
         * @return 完整的授权 URL
         */
        public String buildAuthorizationUrl() {
            return OAuthUrlBuilder.buildAuthorizationUrl(
                    baseUrl, clientId, redirectUri, state, codeChallenge, scope
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Builder builder = (Builder) o;
            return Objects.equals(baseUrl, builder.baseUrl)
                    && Objects.equals(clientId, builder.clientId)
                    && Objects.equals(redirectUri, builder.redirectUri)
                    && Objects.equals(state, builder.state)
                    && Objects.equals(codeChallenge, builder.codeChallenge)
                    && Objects.equals(scope, builder.scope);
        }

        @Override
        public int hashCode() {
            return Objects.hash(baseUrl, clientId, redirectUri, state, codeChallenge, scope);
        }
    }
}
