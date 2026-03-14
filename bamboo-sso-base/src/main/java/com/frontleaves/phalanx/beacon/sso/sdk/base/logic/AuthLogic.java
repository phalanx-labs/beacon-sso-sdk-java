package com.frontleaves.phalanx.beacon.sso.sdk.base.logic;

import com.frontleaves.phalanx.beacon.sso.sdk.base.grpc.SsoGrpcAuthClient;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.ChangePasswordRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.ChangePasswordResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.PasswordLoginRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.PasswordLoginResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RegisterByEmailRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RegisterByEmailResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 认证业务逻辑组件
 * <p>
 * 通过 gRPC 认证服务处理邮箱注册、密码登录与修改密码。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
public class AuthLogic {

    private final SsoGrpcAuthClient ssoGrpcAuthClient;

    public AuthLogic(SsoGrpcAuthClient grpcAuthClient) {
        ssoGrpcAuthClient = grpcAuthClient;
    }

    public RegisterByEmailResponse registerByEmail(RegisterByEmailRequest request) {
        log.info("RegisterByEmail - 处理邮箱注册请求");
        return ssoGrpcAuthClient.registerByEmail(request);
    }

    public PasswordLoginResponse passwordLogin(PasswordLoginRequest request) {
        log.info("PasswordLogin - 处理密码登录请求");
        return ssoGrpcAuthClient.passwordLogin(request);
    }

    public ChangePasswordResponse changePassword(ChangePasswordRequest request) {
        log.info("ChangePassword - 处理修改密码请求");
        return ssoGrpcAuthClient.changePassword(request);
    }
}
