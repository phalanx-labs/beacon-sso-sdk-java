package com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller;

import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoAccountApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoHeaderConstants;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.account.ChangePasswordRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.account.PasswordLoginRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.account.RegisterEmailRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.account.RevokeTokenRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.account.LoginResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.account.RegisterResult;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.utility.SsoSecurityUtil;
import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.mvc.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * 账户相关控制器
 * <p>
 * 提供用户账户管理的 HTTP 端点，包括邮箱注册、密码登录、修改密码和注销登出。
 * 所有请求体参数均通过 Jakarta Validation 注解进行自动校验，
 * 校验失败时由 {@link com.frontleaves.phalanx.beacon.sso.sdk.springboot.exception.GlobalExceptionHandler} 统一处理。
 * </p>
 *
 * <p><b>端点列表：</b></p>
 * <ul>
 *   <li>POST /account/register/email - 通过邮箱注册新用户</li>
 *   <li>POST /account/login/password - 通过用户名和密码登录</li>
 *   <li>POST /account/password/change - 修改用户密码</li>
 *   <li>POST /account/logout - 注销当前用户（撤销 Token）</li>
 * </ul>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RestController
@RequestMapping("${beacon.sso.account-path:/account}")
@RequiredArgsConstructor
public class AccountController {

    private final SsoApi ssoApi;

    /**
     * 邮箱注册
     * <p>
     * 通过邮箱地址和验证码注册新用户。验证码需预先通过
     * {@code POST /public/register/email/code} 端点获取。
     * 可选提供用户名和昵称，若未提供则由服务端自动生成。
     * </p>
     *
     * @param request 邮箱注册请求体，包含邮箱、验证码和密码等必填信息
     * @return 注册成功的响应，包含用户 ID 和初始 Token
     */
    @PostMapping("/register/email")
    public ResponseEntity<BaseResponse<RegisterResult>> registerByEmail(
            @RequestBody @Valid RegisterEmailRequest request
    ) {
        log.info("处理邮箱注册请求");

        try {
            RegisterResult data = ssoApi.account().registerByEmail(request);
            return ResultUtil.success("注册成功", data);
        } catch (Exception e) {
            log.warn("Register by email failed: {}", e.getMessage(), e);
            ErrorCode errorCode = this.mapExceptionToErrorCode(e);
            return ResultUtil.error(errorCode, e.getMessage(), null);
        }
    }

    /**
     * 密码登录
     * <p>
     * 通过用户名（或邮箱/手机号）和密码进行登录认证。
     * 登录成功后返回访问令牌（access_token）和可选的刷新令牌（refresh_token）。
     * 可选提供客户端 IP 和 User-Agent 信息，用于服务端安全审计。
     * </p>
     *
     * @param request 密码登录请求体，包含用户名、密码和授权范围
     * @return 登录成功的响应，包含访问令牌、令牌类型、过期时间等信息
     */
    @PostMapping("/login/password")
    public ResponseEntity<BaseResponse<LoginResult>> passwordLogin(
            @RequestBody @Valid PasswordLoginRequest request
    ) {
        log.info("处理密码登录请求");

        try {
            LoginResult data = ssoApi.account().passwordLogin(request);
            return ResultUtil.success("登录成功", data);
        } catch (Exception e) {
            log.warn("Password login failed: {}", e.getMessage(), e);
            ErrorCode errorCode = this.mapExceptionToErrorCode(e);
            return ResultUtil.error(errorCode, e.getMessage(), null);
        }
    }

    /**
     * 修改密码
     * <p>
     * 修改指定用户的登录密码。必须提供用户 ID 和新密码，
     * 旧密码为可选字段（取决于服务端配置是否要求验证旧密码）。
     * </p>
     *
     * @param request 修改密码请求体，包含用户 ID、新密码和可选的旧密码
     * @return 修改密码的结果响应
     */
    @PostMapping("/password/change")
    public ResponseEntity<BaseResponse<Void>> changePassword(
            @RequestBody @Valid ChangePasswordRequest request
    ) {
        log.info("处理修改密码请求");

        try {
            ssoApi.account().changePassword(request);
            return ResultUtil.success("修改密码成功", null);
        } catch (Exception e) {
            log.warn("Change password failed: {}", e.getMessage(), e);
            ErrorCode errorCode = this.mapExceptionToErrorCode(e);
            return ResultUtil.error(errorCode, e.getMessage(), null);
        }
    }

    /**
     * 注销（登出）
     * <p>
     * 撤销当前用户的访问令牌，使当前会话失效。
     * 支持通过 Authorization 请求头或请求属性（Filter 注入）获取 Token。
     * 可选通过请求体指定 Token 类型提示（{@code tokenTypeHint}），用于精确指定撤销的令牌类型。
     * </p>
     *
     * @param authorization Authorization 请求头中的 Bearer Token（可选）
     * @param request      注销请求体（可选，包含 tokenTypeHint）
     * @param httpRequest  HTTP 请求对象，用于获取 Filter 注入的 Token
     * @return 注销结果响应
     */
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> revokeToken(
            @RequestHeader(value = SsoHeaderConstants.AUTHORIZATION, required = false) String authorization,
            @RequestBody(required = false) RevokeTokenRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("处理注销令牌请求");

        // 获取 access token：优先从 header 获取，其次从请求属性获取
        Optional<String> tokenOpt = Optional.ofNullable(authorization).filter(s -> s != null && !s.isBlank());
        if (tokenOpt.isEmpty()) {
            tokenOpt = SsoSecurityUtil.getCurrentToken(httpRequest);
        }
        if (tokenOpt.isEmpty()) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少 Access Token", null);
        }

        String accessToken = tokenOpt.get();

        // 构建 SDK Request
        RevokeTokenRequest sdkRequest = RevokeTokenRequest.builder()
                .token(accessToken)
                .tokenTypeHint(request != null ? request.getTokenTypeHint() : null)
                .build();

        try {
            ssoApi.account().revokeToken(accessToken, sdkRequest);
            return ResultUtil.success("注销成功", null);
        } catch (Exception e) {
            log.warn("Revoke token failed: {}", e.getMessage(), e);
            ErrorCode errorCode = this.mapExceptionToErrorCode(e);
            return ResultUtil.error(errorCode, e.getMessage(), null);
        }
    }

    private ErrorCode mapExceptionToErrorCode(Exception e) {
        String message = e.getMessage();
        if (message == null) {
            return ErrorCode.OPERATION_FAILED;
        }
        if (message.contains("Missing") || message.contains("missing")) {
            return ErrorCode.PARAMETER_MISSING;
        }
        if (message.contains("token") || message.contains("Token")) {
            return ErrorCode.UNAUTHORIZED;
        }
        if (message.contains("configuration") || message.contains("Configuration")) {
            return ErrorCode.CONFIGURATION_ERROR;
        }
        return ErrorCode.OPERATION_FAILED;
    }
}
