package com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoHeaderConstants;
import com.frontleaves.phalanx.beacon.sso.sdk.base.logic.UserLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.Role;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.User;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.dto.UserInfoDTO;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.dto.UserRoleDTO;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * 用户信息控制器
 * <p>
 * 提供获取当前登录用户信息的 HTTP 端点。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RestController
@RequestMapping("${beacon.sso.user-path:/user}")
@RequiredArgsConstructor
public class UserController {

    private final UserLogic userLogic;

    /**
     * 获取当前用户信息
     *
     * @param authorization Authorization 请求头
     * @param request       HTTP 请求对象
     * @return 当前用户信息响应
     */
    @GetMapping("/userinfo")
    public ResponseEntity<BaseResponse<UserInfoDTO>> getCurrentUser(
            @RequestHeader(value = SsoHeaderConstants.AUTHORIZATION, required = false) String authorization,
            HttpServletRequest request
    ) {
        log.info("Processing current user request");

        Optional<String> tokenOpt = Optional.ofNullable(authorization).filter(StringUtils::hasText);
        if (tokenOpt.isEmpty()) {
            tokenOpt = SsoSecurityUtil.getCurrentToken(request);
        }
        if (tokenOpt.isEmpty()) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "Missing access token", null);
        }

        try {
            User user = userLogic.getCurrentUser(tokenOpt.get());
            UserInfoDTO data = this.toUserInfo(user);
            return ResultUtil.success("获取用户信息成功", data);
        } catch (Exception e) {
            log.warn("Failed to fetch current user: {}", e.getMessage(), e);
            ErrorCode errorCode = this.mapExceptionToErrorCode(e);
            return ResultUtil.error(errorCode, e.getMessage(), null);
        }
    }

    private UserInfoDTO toUserInfo(User user) {
        if (user == null) {
            return null;
        }

        List<UserRoleDTO> roles = user.getRolesList().stream()
                .map(this::toUserRole)
                .toList();

        return UserInfoDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.hasEmail() ? user.getEmail() : null)
                .phone(user.hasPhone() ? user.getPhone() : null)
                .avatar(user.hasAvatar() ? user.getAvatar() : null)
                .gender(user.getGender())
                .birthday(user.hasBirthday() ? user.getBirthday() : null)
                .status(user.getStatus())
                .emailVerified(user.getIsEmailVerified())
                .phoneVerified(user.getIsPhoneVerified())
                .needResetPassword(user.getNeedResetPassword())
                .lastLoginAt(user.hasLastLoginAt() ? user.getLastLoginAt() : null)
                .lastLoginIp(user.hasLastLoginIp() ? user.getLastLoginIp() : null)
                .roles(roles.isEmpty() ? null : roles)
                .build();
    }

    private UserRoleDTO toUserRole(Role role) {
        if (role == null) {
            return null;
        }
        return UserRoleDTO.builder()
                .code(role.getCode())
                .name(role.getName())
                .description(role.hasDescription() ? role.getDescription() : null)
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
