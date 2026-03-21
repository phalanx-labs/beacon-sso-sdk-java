package com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller;

import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.oauth.TokenResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.oauth.ValidateResult;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.logic.AuthLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.logic.UserLogic;
import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.mvc.ResultUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * OAuth 认证控制器
 * <p>
 * 提供 OAuth 2.0 授权码流程的 HTTP 端点，包括登录、回调、登出和状态查询。
 * 支持 PKCE (Proof Key for Code Exchange) 扩展以增强安全性。
 * 所有响应均使用 {@link ResultUtil} 构建标准的 {@link BaseResponse} 格式。
 * </p>
 *
 * <p><b>端点列表：</b></p>
 * <ul>
 *   <li>GET /oauth/login - 重定向到 SSO 授权页面</li>
 *   <li>GET /oauth/callback - 处理 SSO 回调，获取 token</li>
 *   <li>GET /oauth/logout - 登出（撤销 token，清除会话）</li>
 *   <li>GET /oauth/status - 获取当前认证状态</li>
 * </ul>
 *
 * @author xiao_lfeng
 * @since 0.0.1-SNAPSHOT
 */
@Slf4j
@RestController
@RequestMapping("${beacon.sso.controller.path-prefix:/api/v1/beacon}/auth")
@RequiredArgsConstructor
public class BeaconSsoAuthController {

    /**
     * Session 中存储 Token 的键名
     */
    private static final String SESSION_TOKEN_KEY = "OAUTH_TOKEN";

    /**
     * Session 中存储用户信息的键名
     */
    private static final String SESSION_USER_KEY = "OAUTH_USER";

    private final AuthLogic authLogic;
    private final UserLogic userLogic;

    /**
     * 登录端点 - 重定向到 SSO 授权页面
     * <p>
     * 生成授权 URL 并重定向用户到 SSO 服务器的授权页面。
     * 自动生成 state 参数和 PKCE 相关参数以防止 CSRF 和授权码截获攻击。
     * </p>
     *
     * @return 重定向到 SSO 授权页面的 RedirectView
     */
    @GetMapping("/login")
    public RedirectView login() {
        log.info("发起 OAuth 登录流程");

        String authorizationUrl = authLogic.generateAuthorizationUrl().block();

        log.debug("重定向到授权 URL: {}", authorizationUrl);
        return new RedirectView(authorizationUrl);
    }

    /**
     * 回调端点 - 处理 SSO 回调，获取 token
     * <p>
     * 接收 SSO 服务器的授权码回调，验证 state 参数，
     * 使用授权码交换访问令牌，并将令牌存储到会话中。
     * </p>
     *
     * @param code    授权码
     * @param state   状态参数，用于防止 CSRF 攻击
     * @param error   错误码（如果有）
     * @param errorDescription 错误描述（如果有）
     * @param session HTTP 会话
     * @return 包含令牌信息或错误的标准响应
     */
    @GetMapping("/callback")
    public ResponseEntity<BaseResponse<TokenResult>> callback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "error_description", required = false) String errorDescription,
            HttpSession session
    ) {
        log.info("Received OAuth callback - code: {}, state: {}", code != null ? "***" : null, maskState(state));

        // 检查是否有错误响应
        if (error != null) {
            log.warn("OAuth callback received error: {} - {}", error, errorDescription);
            return ResultUtil.error(ErrorCode.UNAUTHORIZED, errorDescription, null);
        }

        // 验证必要参数
        if (code == null || code.isEmpty()) {
            log.warn("OAuth 回调缺少授权码");
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少授权码", null);
        }

        try {
            // 处理回调，交换令牌
            TokenResult token = authLogic.handleCallback(code, state).block();

            if (token != null) {
                // 存储令牌到 Session
                session.setAttribute(SESSION_TOKEN_KEY, token);

                log.info("OAuth authentication successful, token stored in session");

                return ResultUtil.success("OAuth 认证成功", token);
            } else {
                log.error("OAuth token exchange returned null");
                return ResultUtil.error(ErrorCode.OPERATION_FAILED, "授权码交换令牌失败", null);
            }
        } catch (Exception e) {
            log.error("OAuth callback processing failed: {}", e.getMessage(), e);
            // 根据异常类型细化错误码映射
            ErrorCode errorCode = mapExceptionToErrorCode(e);
            return ResultUtil.error(errorCode, e.getMessage(), null);
        }
    }

    /**
     * 登出端点 - 撤销 token 并清除会话
     * <p>
     * 撤销当前用户的访问令牌和刷新令牌，清除会话中的认证信息。
     * </p>
     *
     * @param session HTTP 会话
     * @return 登出结果响应
     */
    @GetMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(HttpSession session) {
        log.info("Processing logout request");

        // 从 Session 获取 Token
        TokenResult token = (TokenResult) session.getAttribute(SESSION_TOKEN_KEY);

        if (token != null) {
            // 撤销访问令牌
            if (token.getAccessToken() != null) {
                authLogic.revokeToken(token.getAccessToken(), "access_token").block();
                log.debug("Access token revoked");
            }

            // 撤销刷新令牌
            if (token.getRefreshToken() != null) {
                authLogic.revokeToken(token.getRefreshToken(), "refresh_token").block();
                log.debug("Refresh token revoked");
            }
        }

        // 清除 Session
        session.removeAttribute(SESSION_TOKEN_KEY);
        session.removeAttribute(SESSION_USER_KEY);

        // 使整个会话失效
        session.invalidate();

        log.info("Logout completed");

        return ResultUtil.success("登出成功", null);
    }

    /**
     * 状态端点 - 获取当前认证状态
     * <p>
     * 检查当前用户是否已认证，返回认证状态和用户信息（如果已认证）。
     * </p>
     *
     * @param session HTTP 会话
     * @return 认证状态响应
     */
    @GetMapping("/status")
    public ResponseEntity<BaseResponse<ValidateResult>> status(HttpSession session) {
        log.debug("Checking authentication status");

        // 从 Session 获取 Token
        TokenResult token = (TokenResult) session.getAttribute(SESSION_TOKEN_KEY);

        if (token != null && token.getAccessToken() != null) {
            // 检查令牌是否过期
            boolean isExpired = isTokenExpired(token);

            if (!isExpired) {
                // 调用 validate 接口验证 token
                try {
                    ValidateResult validateResult = authLogic.validateToken(token.getAccessToken()).block();
                    log.debug("User is authenticated");
                    return ResultUtil.success("用户已认证", validateResult);
                } catch (Exception e) {
                    log.debug("Token validation failed: {}", e.getMessage());
                    // 验证失败，清除令牌
                    session.removeAttribute(SESSION_TOKEN_KEY);
                }
            } else {
                log.debug("Token has expired");
                // 清除过期的令牌
                session.removeAttribute(SESSION_TOKEN_KEY);
            }
        }

        log.debug("User is not authenticated");
        return ResultUtil.success("未认证或令牌已过期", null);
    }

    /**
     * 检查令牌是否过期
     *
     * @param token OAuth 令牌
     * @return 如果令牌已过期返回 true
     */
    private boolean isTokenExpired(TokenResult token) {
        if (token == null || token.getCreatedAt() <= 0) {
            return true;
        }

        long expiresAt = token.getCreatedAt() + (token.getExpiresIn() * 1000L);
        // 提前 30 秒认为过期，避免边界情况
        return System.currentTimeMillis() >= (expiresAt - 30000);
    }

    /**
     * 计算剩余过期时间（秒）
     *
     * @param token OAuth 令牌
     * @return 剩余过期时间（秒），如果已过期返回 0
     */
    private long calculateRemainingExpiresIn(TokenResult token) {
        if (token == null || token.getCreatedAt() <= 0) {
            return 0;
        }

        long expiresAt = token.getCreatedAt() + (token.getExpiresIn() * 1000L);
        long remainingMs = expiresAt - System.currentTimeMillis();
        return Math.max(0, remainingMs / 1000);
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

    /**
     * 根据异常类型映射到合适的 ErrorCode
     *
     * @param e 异常
     * @return 对应的 ErrorCode
     */
    private ErrorCode mapExceptionToErrorCode(Exception e) {
        String message = e.getMessage();
        if (message == null) {
            return ErrorCode.UNAUTHORIZED;
        }

        // 根据异常消息内容细化错误码
        if (message.contains("state") || message.contains("State")) {
            return ErrorCode.PARAMETER_INVALID;
        }
        if (message.contains("expired") || message.contains("Expired")) {
            return ErrorCode.EXPIRED;
        }
        if (message.contains("configuration") || message.contains("Configuration")) {
            return ErrorCode.CONFIGURATION_ERROR;
        }
        if (message.contains("token") || message.contains("Token")) {
            return ErrorCode.UNAUTHORIZED;
        }

        return ErrorCode.UNAUTHORIZED;
    }
}
