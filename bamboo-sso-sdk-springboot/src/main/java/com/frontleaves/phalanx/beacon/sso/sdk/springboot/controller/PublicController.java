package com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller;

import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.models.request.SendRegisterEmailCodeRequest;
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
 * 公开接口控制器
 * <p>
 * 提供无需用户认证的公开 HTTP 端点，如发送注册验证码。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RestController
@RequestMapping("${beacon.sso.public-path:/public}")
@RequiredArgsConstructor
public class PublicController {

    private final SsoRequest ssoRequest;

    /**
     * 发送注册邮箱验证码
     * <p>
     * 向指定邮箱地址发送注册验证码，用于后续的邮箱注册流程。
     * </p>
     *
     * @param request 请求体，包含目标邮箱地址
     * @return 发送结果响应
     */
    @PostMapping("/register/email/code")
    public ResponseEntity<BaseResponse<Void>> sendRegisterEmailCode(
            @RequestBody SendRegisterEmailCodeRequest request
    ) {
        log.info("处理发送注册邮箱验证码请求");

        if (request == null) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少请求体", null);
        }
        if (!StringUtils.hasText(request.getEmail())) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少邮箱地址", null);
        }

        com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.SendRegisterEmailCodeRequest grpcRequest = com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.SendRegisterEmailCodeRequest.newBuilder()
                .setEmail(request.getEmail())
                .build();

        try {
            ssoRequest.publicService().sendRegisterEmailCode(grpcRequest);
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
