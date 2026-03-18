package com.frontleaves.phalanx.beacon.sso.sdk.base.api.grpc;

import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.GrpcUtil;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.PublicServiceGrpc;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.SendRegisterEmailCodeRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.SendRegisterEmailCodeResponse;
import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SSO gRPC 公共服务客户端
 * <p>
 * 封装公共相关的 gRPC 调用，直接使用 protobuf 原生类型。
 * 所有方法的形参和返回值均为 protobuf 生成的类型。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RequiredArgsConstructor
public class SsoGrpcPublicClient {

    private final ManagedChannel channel;
    private final BeaconSsoProperties properties;

    /**
     * 发送注册邮箱验证码
     *
     * @param request protobuf 验证码发送请求
     * @return protobuf 发送响应
     */
    public SendRegisterEmailCodeResponse sendRegisterEmailCode(SendRegisterEmailCodeRequest request) {
        log.debug("[gRPC] 发送邮箱验证码: email={}", request.getEmail());

        var stub = GrpcUtil.attachAppHeaders(
                PublicServiceGrpc.newBlockingStub(channel),
                properties.getGrpc().getAppAccessId(),
                properties.getGrpc().getAppSecretKey()
        );

        return stub.sendRegisterEmailCode(request);
    }
}
