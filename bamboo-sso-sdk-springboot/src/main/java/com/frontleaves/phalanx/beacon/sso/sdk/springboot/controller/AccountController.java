package com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoHeaderConstants;
import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.PasswordLoginResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RegisterByEmailResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.models.request.ChangePasswordRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.models.request.PasswordLoginRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.models.request.RevokeTokenRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.models.request.RegisterByEmailRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.models.RegisterByEmail;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.models.PasswordLogin;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.utility.SsoSecurityUtil;
import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.mvc.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * 账户相关控制器
 * <p>
 * 提供邮箱注册、密码登录与修改密码的 HTTP 端点。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RestController
@RequestMapping("${beacon.sso.account-path:/account}")
@RequiredArgsConstructor
public class AccountController {

    private final SsoRequest ssoRequest;

    /**
     * 邮箱注册
     *
     * @param request 请求体
     * @return 注册响应
     */
    @PostMapping("/register/email")
    public ResponseEntity<BaseResponse<RegisterByEmail>> registerByEmail(
            @RequestBody RegisterByEmailRequest request
    ) {
        log.info("处理邮箱注册请求");

        if (request == null) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少请求体", null);
        }
        if (!StringUtils.hasText(request.getEmail())) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少邮箱地址", null);
        }
        if (!StringUtils.hasText(request.getCode())) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少验证码", null);
        }
        if (!StringUtils.hasText(request.getPassword())) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少密码", null);
        }

        com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RegisterByEmailRequest.Builder builder = com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RegisterByEmailRequest.newBuilder()
                .setEmail(request.getEmail())
                .setCode(request.getCode())
                .setPassword(request.getPassword());
        if (StringUtils.hasText(request.getUsername())) {
            builder.setUsername(request.getUsername());
        }
        if (StringUtils.hasText(request.getNickname())) {
            builder.setNickname(request.getNickname());
        }

        try {
            RegisterByEmailResponse response = ssoRequest.auth().registerByEmail(builder.build());
            RegisterByEmail data = this.toRegisterResponse(response);
            return ResultUtil.success("注册成功", data);
        } catch (Exception e) {
            log.warn("Register by email failed: {}", e.getMessage(), e);
            ErrorCode errorCode = this.mapExceptionToErrorCode(e);
            return ResultUtil.error(errorCode, e.getMessage(), null);
        }
    }

    /**
     * 密码登录
     *
     * @param request 请求体
     * @return 登录响应
     */
    @PostMapping("/login/password")
    public ResponseEntity<BaseResponse<PasswordLogin>> passwordLogin(
            @RequestBody PasswordLoginRequest request
    ) {
        log.info("处理密码登录请求");

        if (request == null) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少请求体", null);
        }
        if (!StringUtils.hasText(request.getUsername())) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少用户名", null);
        }
        if (!StringUtils.hasText(request.getPassword())) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少密码", null);
        }
        if (!StringUtils.hasText(request.getScope())) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少作用域", null);
        }

        com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.PasswordLoginRequest.Builder builder = com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.PasswordLoginRequest.newBuilder()
                .setUsername(request.getUsername())
                .setPassword(request.getPassword())
                .setScope(request.getScope());
        if (StringUtils.hasText(request.getClientIp())) {
            builder.setClientIp(request.getClientIp());
        }
        if (StringUtils.hasText(request.getUserAgent())) {
            builder.setUserAgent(request.getUserAgent());
        }

        try {
            PasswordLoginResponse response = ssoRequest.auth().passwordLogin(builder.build());
            PasswordLogin data = this.toLoginResponse(response);
            return ResultUtil.success("登录成功", data);
        } catch (Exception e) {
            log.warn("Password login failed: {}", e.getMessage(), e);
            ErrorCode errorCode = this.mapExceptionToErrorCode(e);
            return ResultUtil.error(errorCode, e.getMessage(), null);
        }
    }

    /**
     * 修改密码
     *
     * @param request 请求体
     * @return 修改响应
     */
    @PostMapping("/password/change")
    public ResponseEntity<BaseResponse<Void>> changePassword(@RequestBody ChangePasswordRequest request) {
        log.info("处理修改密码请求");

        if (request == null) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少请求体", null);
        }
        if (!StringUtils.hasText(request.getUserId())) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少用户 ID", null);
        }
        if (!StringUtils.hasText(request.getNewPassword())) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少新密码", null);
        }

        com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.ChangePasswordRequest.Builder builder = com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.ChangePasswordRequest.newBuilder()
                .setUserId(request.getUserId())
                .setNewPassword(request.getNewPassword());
        if (StringUtils.hasText(request.getOldPassword())) {
            builder.setOldPassword(request.getOldPassword());
        }

        try {
            ssoRequest.auth().changePassword(builder.build());
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
     * 撤销当前用户的访问令牌。支持通过 Authorization 请求头或请求属性获取 Token。
     * </p>
     *
     * @param authorization Authorization 请求头
     * @param request      请求体（可选，包含 tokenTypeHint）
     * @param httpRequest  HTTP 请求对象
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
        Optional<String> tokenOpt = Optional.ofNullable(authorization).filter(StringUtils::hasText);
        if (tokenOpt.isEmpty()) {
            tokenOpt = SsoSecurityUtil.getCurrentToken(httpRequest);
        }
        if (tokenOpt.isEmpty()) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少 Access Token", null);
        }

        String accessToken = tokenOpt.get();
        com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RevokeTokenRequest.Builder revokeTokenRequestBuilder = com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RevokeTokenRequest.newBuilder();
        if (request != null && StringUtils.hasText(request.getTokenTypeHint())) {
            revokeTokenRequestBuilder.setTokenTypeHint(request.getTokenTypeHint());
        }

        try {
            ssoRequest.auth().revokeToken(accessToken, revokeTokenRequestBuilder.build());
            return ResultUtil.success("注销成功", null);
        } catch (Exception e) {
            log.warn("Revoke token failed: {}", e.getMessage(), e);
            ErrorCode errorCode = this.mapExceptionToErrorCode(e);
            return ResultUtil.error(errorCode, e.getMessage(), null);
        }
    }

    private RegisterByEmail toRegisterResponse(RegisterByEmailResponse response) {
        if (response == null) {
            return null;
        }
        return RegisterByEmail.builder()
                .userId(response.getUserId())
                .token(response.getToken())
                .build();
    }

    private PasswordLogin toLoginResponse(PasswordLoginResponse response) {
        if (response == null) {
            return null;
        }
        return PasswordLogin.builder()
                .accessToken(response.getAccessToken())
                .tokenType(response.getTokenType())
                .expiresIn(response.getExpiresIn())
                .refreshToken(response.hasRefreshToken() ? response.getRefreshToken() : null)
                .scope(response.hasScope() ? response.getScope() : null)
                .idToken(response.hasIdToken() ? response.getIdToken() : null)
                .build();
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
