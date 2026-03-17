package com.frontleaves.phalanx.beacon.sso.sdk.base.api;

import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.SendRegisterEmailCodeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 公共操作（gRPC-only）
 * <p>
 * 封装无需用户认证的公开操作。
 * 所有方法均通过 gRPC 协议与 SSO 服务通信。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RequiredArgsConstructor
public class SsoPublicApi {

    private final SsoRequest ssoRequest;

    /**
     * 发送注册邮箱验证码
     *
     * @param request 验证码发送请求
     */
    public void sendRegisterEmailCode(SendRegisterEmailCodeRequest request) {
        log.debug("发送注册邮箱验证码: email={}", request.getEmail());
        ssoRequest.publicService().sendRegisterEmailCode(request);
    }
}
