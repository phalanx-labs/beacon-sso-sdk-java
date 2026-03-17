package com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller;

import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoUserApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoHeaderConstants;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthUserinfo;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.SsoRole;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.SsoUserDetail;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetUserByIDRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.models.UserInfo;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.models.UserRole;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.utility.SsoSecurityUtil;
import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.mvc.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * 用户信息控制器
 * <p>
 * 提供用户信息查询的 HTTP 端点，包括获取当前登录用户信息和根据用户 ID 获取指定用户信息。
 * 需要通过 Authorization 请求头或 Filter 注入的方式提供有效的 Access Token。
 * </p>
 *
 * <p><b>端点列表：</b></p>
 * <ul>
 *   <li>GET /user/userinfo - 获取当前登录用户信息</li>
 *   <li>GET /user/{userId} - 根据用户 ID 获取指定用户信息</li>
 * </ul>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RestController
@RequestMapping("${beacon.sso.user-path:/user}")
@RequiredArgsConstructor
public class UserController {

    private final SsoUserApi ssoUserApi;

    /**
     * 获取当前用户信息
     * <p>
     * 根据 Authorization 请求头或 Filter 注入的 Token 获取当前已认证用户的详细信息。
     * Token 优先从 Authorization 请求头获取，若不存在则从请求属性中获取（由 Filter 注入）。
     * </p>
     *
     * @param authorization Authorization 请求头中的 Bearer Token（可选）
     * @param request       HTTP 请求对象，用于获取 Filter 注入的 Token
     * @return 当前用户信息响应，包含用户名、昵称、邮箱、角色等详细信息
     */
    @GetMapping("/userinfo")
    public ResponseEntity<BaseResponse<UserInfo>> getCurrentUser(
            @RequestHeader(value = SsoHeaderConstants.AUTHORIZATION, required = false) String authorization,
            HttpServletRequest request
    ) {
        log.info("处理获取当前用户信息请求");

        Optional<String> tokenOpt = Optional.ofNullable(authorization).filter(StringUtils::hasText);
        if (tokenOpt.isEmpty()) {
            tokenOpt = SsoSecurityUtil.getCurrentToken(request);
        }
        if (tokenOpt.isEmpty()) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少 Access Token", null);
        }

        try {
            OAuthUserinfo oauthUserinfo = ssoUserApi.getCurrentUser(tokenOpt.get()).block();
            UserInfo data = this.toUserInfo(oauthUserinfo);
            return ResultUtil.success("获取用户信息成功", data);
        } catch (Exception e) {
            log.warn("获取当前用户信息失败: {}", e.getMessage(), e);
            ErrorCode errorCode = this.mapExceptionToErrorCode(e);
            return ResultUtil.error(errorCode, e.getMessage(), null);
        }
    }

    /**
     * 根据 ID 获取用户信息
     * <p>
     * 根据指定的用户 ID 获取该用户的详细信息。
     * 需要提供有效的 Access Token 以完成认证。
     * </p>
     *
     * @param userId       目标用户 ID（路径参数）
     * @param authorization Authorization 请求头中的 Bearer Token（可选）
     * @param request      HTTP 请求对象，用于获取 Filter 注入的 Token
     * @return 指定用户的详细信息响应
     */
    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse<UserInfo>> getUserById(
            @PathVariable String userId,
            @RequestHeader(value = SsoHeaderConstants.AUTHORIZATION, required = false) String authorization,
            HttpServletRequest request
    ) {
        log.info("Processing get user by ID request: {}", userId);

        if (!StringUtils.hasText(userId)) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少用户 ID", null);
        }

        Optional<String> tokenOpt = Optional.ofNullable(authorization).filter(StringUtils::hasText);
        if (tokenOpt.isEmpty()) {
            tokenOpt = SsoSecurityUtil.getCurrentToken(request);
        }
        if (tokenOpt.isEmpty()) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少 Access Token", null);
        }

        try {
            GetUserByIDRequest grpcRequest = GetUserByIDRequest.newBuilder()
                    .setUserId(userId)
                    .build();
            SsoUserDetail userDetail = ssoUserApi.getUserById(tokenOpt.get(), grpcRequest);
            UserInfo data = this.toUserInfo(userDetail);
            return ResultUtil.success("获取用户信息成功", data);
        } catch (Exception e) {
            log.warn("按 ID 获取用户信息失败: {}", e.getMessage(), e);
            ErrorCode errorCode = this.mapExceptionToErrorCode(e);
            return ResultUtil.error(errorCode, e.getMessage(), null);
        }
    }

    private UserInfo toUserInfo(OAuthUserinfo userinfo) {
        if (userinfo == null) {
            return null;
        }

        return UserInfo.builder()
                .id(userinfo.getSub())
                .username(userinfo.getPreferredUsername())
                .nickname(userinfo.getName())
                .email(userinfo.getEmail())
                .emailVerified(userinfo.getEmailVerified())
                .avatar(userinfo.getPicture())
                .build();
    }

    private UserInfo toUserInfo(SsoUserDetail detail) {
        if (detail == null) {
            return null;
        }

        List<UserRole> roles = detail.getRoles() == null ? null : detail.getRoles().stream()
                .map(this::toUserRole)
                .toList();

        return UserInfo.builder()
                .id(detail.getId())
                .username(detail.getUsername())
                .nickname(detail.getNickname())
                .email(detail.getEmail())
                .phone(detail.getPhone())
                .avatar(detail.getAvatar())
                .gender(detail.getGender() == 0 ? null : detail.getGender())
                .birthday(detail.getBirthday())
                .status(detail.getStatus() == 0 ? null : detail.getStatus())
                .emailVerified(detail.isEmailVerified())
                .phoneVerified(detail.isPhoneVerified())
                .needResetPassword(detail.isNeedResetPassword())
                .lastLoginAt(detail.getLastLoginAt())
                .lastLoginIp(detail.getLastLoginIp())
                .roles(roles)
                .build();
    }

    private UserRole toUserRole(SsoRole role) {
        if (role == null) {
            return null;
        }
        return UserRole.builder()
                .code(role.getCode())
                .name(role.getName())
                .description(role.getDescription())
                .build();
    }

    private ErrorCode mapExceptionToErrorCode(Exception e) {
        String message = e.getMessage();
        if (message == null) {
            return ErrorCode.UNAUTHORIZED;
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
