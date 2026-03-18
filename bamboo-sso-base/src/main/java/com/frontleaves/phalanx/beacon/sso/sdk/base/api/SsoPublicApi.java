package com.frontleaves.phalanx.beacon.sso.sdk.base.api;

import com.frontleaves.phalanx.beacon.sso.sdk.base.api.grpc.SsoGrpcPublicClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.normal.SendEmailCodeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 公共操作聚合层（gRPC-only）
 * <p>
 * 封装无需用户认证的公开操作。
 * 负责将 SDK Request 转换为 protobuf Request，调用 gRPC 客户端。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RequiredArgsConstructor
public class SsoPublicApi {

    private final SsoGrpcPublicClient grpcClient;

    /**
     * 发送邮箱验证码
     *
     * @param request 验证码发送请求
     */
    public void sendEmailCode(SendEmailCodeRequest request) {
        log.debug("[聚合层] 发送邮箱验证码: email={}", request.getEmail());

        // SDK Request → Protobuf Request
        // 注意：protobuf SendRegisterEmailCodeRequest 只有 email 字段，没有 purpose
        var grpcRequest = com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.SendRegisterEmailCodeRequest.newBuilder()
                .setEmail(request.getEmail())
                .build();

        // 调用 gRPC 客户端
        grpcClient.sendRegisterEmailCode(grpcRequest);
    }
}
