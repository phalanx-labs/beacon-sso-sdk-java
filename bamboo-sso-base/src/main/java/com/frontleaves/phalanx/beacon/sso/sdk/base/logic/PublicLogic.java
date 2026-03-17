package com.frontleaves.phalanx.beacon.sso.sdk.base.logic;

import com.frontleaves.phalanx.beacon.sso.sdk.base.grpc.SsoGrpcPublicClient;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.SendRegisterEmailCodeRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.SendRegisterEmailCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 公共业务逻辑组件
 * <p>
 * 通过 gRPC 公共服务处理无需用户认证的公开接口调用。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
public class PublicLogic {

    private final SsoGrpcPublicClient ssoGrpcPublicClient;

    /**
     * 构造公共业务逻辑组件
     *
     * @param grpcPublicClient gRPC 公共服务客户端
     */
    public PublicLogic(SsoGrpcPublicClient grpcPublicClient) {
        ssoGrpcPublicClient = grpcPublicClient;
    }

    /**
     * 发送注册邮箱验证码
     *
     * @param request 发送注册邮箱验证码请求
     * @return 发送结果响应
     */
    public SendRegisterEmailCodeResponse sendRegisterEmailCode(SendRegisterEmailCodeRequest request) {
        log.info("SendRegisterEmailCode - 发送注册邮箱验证码");
        return ssoGrpcPublicClient.sendRegisterEmailCode(request);
    }
}
