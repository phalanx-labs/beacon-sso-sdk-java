package com.frontleaves.phalanx.beacon.sso.sdk.base.api;

import com.frontleaves.phalanx.beacon.sso.sdk.base.api.grpc.SsoGrpcPublicClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoConfigurationException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.normal.SendEmailCodeRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
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
public class SsoPublicApi {

    private final BeaconSsoProperties properties;
    private final SsoGrpcPublicClient grpcClient;

    public SsoPublicApi(BeaconSsoProperties properties, SsoGrpcPublicClient grpcClient) {
        this.properties = properties;
        this.grpcClient = grpcClient;
    }

    /**
     * 检查 gRPC 是否启用
     */
    private boolean isGrpcEnabled() {
        return properties.getGrpc() != null && properties.getGrpc().isEnabled();
    }

    /**
     * 发送邮箱验证码
     *
     * @param request 验证码发送请求
     */
    public void sendEmailCode(SendEmailCodeRequest request) {
        if (!isGrpcEnabled()) {
            throw new SsoConfigurationException(
                    SsoErrorCode.GRPC_NOT_ENABLED,
                    "sendEmailCode 需要 gRPC 通信，请启用 gRPC 配置"
            );
        }

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
