package com.frontleaves.phalanx.beacon.sso.sdk.base;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoCacheConstants;
import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;
import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoHeaderConstants;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthToken;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthUserinfo;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthIntrospection;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthState;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.PkceUtil;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.StateUtil;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.OAuthUrlBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base 模块单元测试
 *
 * @author Xiao Lfeng &lt;xiaolfeng@x-lf.com&gt;
 */
class BaseModuleTest {

    @Test
    void testConstants() {
        // 验证 Header 常量
        assertEquals("Authorization", SsoHeaderConstants.AUTHORIZATION);
        assertEquals("Bearer ", SsoHeaderConstants.BEARER_PREFIX);
        assertEquals("Content-Type", SsoHeaderConstants.CONTENT_TYPE);

        // 验证缓存常量
        assertEquals("beaconSsoCacheManager", SsoCacheConstants.CACHE_MANAGER_NAME);
        assertEquals("oauthState", SsoCacheConstants.CACHE_OAUTH_STATE);
        assertEquals("userinfo", SsoCacheConstants.CACHE_USERINFO);

        // 验证错误码
        assertEquals("invalid_state", SsoErrorCode.INVALID_STATE.getCode());
        assertEquals("token_expired", SsoErrorCode.TOKEN_EXPIRED.getCode());
    }

    @Test
    void testPkceUtil() {
        // 测试生成 code_verifier
        String codeVerifier = PkceUtil.generateCodeVerifier();
        assertNotNull(codeVerifier);
        assertTrue(codeVerifier.length() >= 43 && codeVerifier.length() <= 128);

        // 测试生成 code_challenge
        String codeChallenge = PkceUtil.generateCodeChallenge(codeVerifier);
        assertNotNull(codeChallenge);
        assertTrue(codeChallenge.length() == 43);

        // 测试验证
        assertTrue(PkceUtil.verifyCodeChallenge(codeVerifier, codeChallenge));
        assertFalse(PkceUtil.verifyCodeChallenge("invalid", codeChallenge));

        // 测试 challenge method
        assertEquals("S256", PkceUtil.getCodeChallengeMethod());
    }

    @Test
    void testStateUtil() {
        // 测试生成 state
        String state = StateUtil.generateState();
        assertNotNull(state);
        assertFalse(state.isEmpty());

        // 测试验证 state
        assertTrue(StateUtil.validateState(state, state));
        assertFalse(StateUtil.validateState(state, "different"));

        // 测试 null 安全
        assertFalse(StateUtil.validateState(null, state));
        assertFalse(StateUtil.validateState(state, null));
    }

    @Test
    void testOAuthUrlBuilder() {
        String baseUrl = "https://sso.example.com";
        String clientId = "test-client";
        String redirectUri = "https://app.example.com/callback";
        String state = "random-state";
        String codeChallenge = "test-challenge";

        // 测试构建授权 URL
        String authUrl = OAuthUrlBuilder.buildAuthorizationUrl(
                baseUrl, clientId, redirectUri, state, codeChallenge, "openid profile"
        );
        assertNotNull(authUrl);
        assertTrue(authUrl.contains("response_type=code"));
        assertTrue(authUrl.contains("client_id=" + clientId));
        assertTrue(authUrl.contains("redirect_uri="));
        assertTrue(authUrl.contains("state=" + state));
        assertTrue(authUrl.contains("code_challenge=" + codeChallenge));
        assertTrue(authUrl.contains("code_challenge_method=S256"));

        // 测试构建其他 URL (baseUrl 已经包含 /oauth 路径时)
        String oauthBaseUrl = baseUrl + "/oauth";
        assertEquals(oauthBaseUrl + "/token", OAuthUrlBuilder.buildTokenUrl(oauthBaseUrl));
        assertEquals(oauthBaseUrl + "/userinfo", OAuthUrlBuilder.buildUserinfoUrl(oauthBaseUrl));
        assertEquals(oauthBaseUrl + "/introspect", OAuthUrlBuilder.buildIntrospectionUrl(oauthBaseUrl));
        assertEquals(oauthBaseUrl + "/revoke", OAuthUrlBuilder.buildRevocationUrl(oauthBaseUrl));

        // 测试 baseUrl 不带 /oauth 路径时
        assertEquals(baseUrl + "/token", OAuthUrlBuilder.buildTokenUrl(baseUrl));
        assertEquals(baseUrl + "/userinfo", OAuthUrlBuilder.buildUserinfoUrl(baseUrl));
    }

    @Test
    void testOAuthToken() {
        OAuthToken token = OAuthToken.builder()
                .accessToken("access-token-123")
                .tokenType("Bearer")
                .expiresIn(3600L)
                .refreshToken("refresh-token-456")
                .scope("openid profile")
                .build();

        assertNotNull(token);
        assertEquals("access-token-123", token.getAccessToken());
        assertEquals("Bearer", token.getTokenType());
        assertEquals(3600L, token.getExpiresIn());
        assertEquals("refresh-token-456", token.getRefreshToken());
        assertEquals("openid profile", token.getScope());
    }

    @Test
    void testOAuthUserinfo() {
        OAuthUserinfo userinfo = OAuthUserinfo.builder()
                .sub("user-123")
                .name("Test User")
                .preferredUsername("testuser")
                .email("test@example.com")
                .emailVerified(true)
                .build();

        assertNotNull(userinfo);
        assertEquals("user-123", userinfo.getSub());
        assertEquals("Test User", userinfo.getName());
        assertEquals("testuser", userinfo.getPreferredUsername());
        assertEquals("test@example.com", userinfo.getEmail());
        assertTrue(userinfo.getEmailVerified());
    }

    @Test
    void testOAuthIntrospection() {
        OAuthIntrospection introspection = OAuthIntrospection.builder()
                .active(true)
                .sub("user-123")
                .username("testuser")
                .clientId("client-123")
                .scope("openid profile")
                .exp(System.currentTimeMillis() / 1000 + 3600)
                .iat(System.currentTimeMillis() / 1000)
                .build();

        assertNotNull(introspection);
        assertTrue(introspection.isActive());
        assertEquals("user-123", introspection.getSub());
        assertEquals("testuser", introspection.getUsername());
        assertEquals("client-123", introspection.getClientId());
    }

    @Test
    void testOAuthState() {
        String state = StateUtil.generateState();
        String codeVerifier = PkceUtil.generateCodeVerifier();

        OAuthState oauthState = OAuthState.builder()
                .state(state)
                .codeVerifier(codeVerifier)
                .redirectUri("https://app.example.com/callback")
                .createdAt(System.currentTimeMillis())
                .expiresAt(System.currentTimeMillis() + 900000) // 15 minutes
                .build();

        assertNotNull(oauthState);
        assertEquals(state, oauthState.getState());
        assertEquals(codeVerifier, oauthState.getCodeVerifier());
        assertNotNull(oauthState.getRedirectUri());
    }
}
