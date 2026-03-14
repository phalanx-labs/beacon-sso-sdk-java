package com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller;

import com.frontleaves.phalanx.beacon.sso.sdk.base.logic.AuthLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.ChangePasswordRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.PasswordLoginRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.PasswordLoginResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RegisterByEmailRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RegisterByEmailResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.dto.ChangePasswordRequestDTO;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.dto.PasswordLoginRequestDTO;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.dto.PasswordLoginResponseDTO;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.dto.RegisterByEmailRequestDTO;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.dto.RegisterByEmailResponseDTO;
import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.mvc.ResultUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private final AuthLogic authLogic;

    /**
     * 邮箱注册
     *
     * @param request 请求体
     * @return 注册响应
     */
    @PostMapping("/register/email")
    public ResponseEntity<BaseResponse<RegisterByEmailResponseDTO>> registerByEmail(
            @RequestBody RegisterByEmailRequestDTO request
    ) {
        log.info("Processing register by email request");

        if (request == null) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "Missing request body", null);
        }
        if (!StringUtils.hasText(request.getEmail())) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "Missing email", null);
        }
        if (!StringUtils.hasText(request.getCode())) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "Missing code", null);
        }
        if (!StringUtils.hasText(request.getPassword())) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "Missing password", null);
        }

        RegisterByEmailRequest.Builder builder = RegisterByEmailRequest.newBuilder()
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
            RegisterByEmailResponse response = authLogic.registerByEmail(builder.build());
            RegisterByEmailResponseDTO data = this.toRegisterResponse(response);
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
    public ResponseEntity<BaseResponse<PasswordLoginResponseDTO>> passwordLogin(
            @RequestBody PasswordLoginRequestDTO request
    ) {
        log.info("Processing password login request");

        if (request == null) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "Missing request body", null);
        }
        if (!StringUtils.hasText(request.getUsername())) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "Missing username", null);
        }
        if (!StringUtils.hasText(request.getPassword())) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "Missing password", null);
        }
        if (!StringUtils.hasText(request.getScope())) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "Missing scope", null);
        }

        PasswordLoginRequest.Builder builder = PasswordLoginRequest.newBuilder()
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
            PasswordLoginResponse response = authLogic.passwordLogin(builder.build());
            PasswordLoginResponseDTO data = this.toLoginResponse(response);
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
    public ResponseEntity<BaseResponse<Void>> changePassword(@RequestBody ChangePasswordRequestDTO request) {
        log.info("Processing change password request");

        if (request == null) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "Missing request body", null);
        }
        if (!StringUtils.hasText(request.getUserId())) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "Missing user_id", null);
        }
        if (!StringUtils.hasText(request.getNewPassword())) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "Missing new_password", null);
        }

        ChangePasswordRequest.Builder builder = ChangePasswordRequest.newBuilder()
                .setUserId(request.getUserId())
                .setNewPassword(request.getNewPassword());
        if (StringUtils.hasText(request.getOldPassword())) {
            builder.setOldPassword(request.getOldPassword());
        }

        try {
            authLogic.changePassword(builder.build());
            return ResultUtil.success("修改密码成功", null);
        } catch (Exception e) {
            log.warn("Change password failed: {}", e.getMessage(), e);
            ErrorCode errorCode = this.mapExceptionToErrorCode(e);
            return ResultUtil.error(errorCode, e.getMessage(), null);
        }
    }

    private RegisterByEmailResponseDTO toRegisterResponse(RegisterByEmailResponse response) {
        if (response == null) {
            return null;
        }
        return RegisterByEmailResponseDTO.builder()
                .userId(response.getUserId())
                .token(response.getToken())
                .build();
    }

    private PasswordLoginResponseDTO toLoginResponse(PasswordLoginResponse response) {
        if (response == null) {
            return null;
        }
        return PasswordLoginResponseDTO.builder()
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
