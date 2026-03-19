package com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller;

import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoPublicApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.normal.SendEmailCodeRequest;
import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.mvc.ResultUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公开接口控制器
 * <p>
 * 提供无需用户认证的公开 HTTP 端点，如发送注册验证码等。
 * 所有请求体参数均通过 Jakarta Validation 注解进行自动校验，
 * 校验失败时由 {@link com.frontleaves.phalanx.beacon.sso.sdk.springboot.exception.GlobalExceptionHandler} 统一处理。
 * </p>
 *
 * <p><b>端点列表：</b></p>
 * <ul>
 *   <li>POST /public/register/email/code - 向指定邮箱发送注册验证码</li>
 * </ul>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RestController
@RequestMapping("${beacon.sso.public-path:/public}")
@RequiredArgsConstructor
public class PublicController {

    private final SsoPublicApi ssoPublicApi;

    /**
     * 发送注册邮箱验证码
     * <p>
     * 向指定邮箱地址发送注册验证码，用于后续的邮箱注册流程。
     * 验证码具有时效性，过期后需重新请求发送。
     * </p>
     *
     * @param request 发送验证码请求体，包含目标邮箱地址
     * @return 发送结果响应
     */
    @PostMapping("/register/email/code")
    public ResponseEntity<BaseResponse<Void>> sendRegisterEmailCode(
            @RequestBody @Valid SendEmailCodeRequest request
    ) {
        log.info("处理发送注册邮箱验证码请求");

        try {
            ssoPublicApi.sendEmailCode(request);
            return ResultUtil.success("验证码发送成功", null);
        } catch (Exception e) {
            log.warn("Send register email code failed: {}", e.getMessage(), e);
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
        if (message.contains("rate") || message.contains("Rate")) {
            return ErrorCode.OPERATION_FAILED;
        }
        if (message.contains("configuration") || message.contains("Configuration")) {
            return ErrorCode.CONFIGURATION_ERROR;
        }
        return ErrorCode.OPERATION_FAILED;
    }
}
