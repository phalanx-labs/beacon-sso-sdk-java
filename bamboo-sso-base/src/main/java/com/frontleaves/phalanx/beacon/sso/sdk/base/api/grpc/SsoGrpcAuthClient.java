package com.frontleaves.phalanx.beacon.sso.sdk.base.api.grpc;

import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.GrpcUtil;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.AuthServiceGrpc;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.ChangePasswordRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.ChangePasswordResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.PasswordLoginRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.PasswordLoginResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RegisterByEmailRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RegisterByEmailResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RevokeTokenRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RevokeTokenResponse;
import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

/**
 * SSO gRPC 认证服务客户端
 * <p>
 * 封装认证相关的 gRPC 调用，直接使用 protobuf 原生类型。
 * 所有方法的形参和返回值均为 protobuf 生成的类型。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RequiredArgsConstructor
public class SsoGrpcAuthClient {

    private final BeaconSsoProperties properties;
    private final ManagedChannel channel;

    /**
     * 通过邮箱注册
     *
     * @param request protobuf 注册请求
     * @return protobuf 注册响应
     */
    public RegisterByEmailResponse registerByEmail(@NonNull RegisterByEmailRequest request) {
        log.debug("[gRPC] 执行邮箱注册: email={}", request.getEmail());

        var stub = GrpcUtil.attachAppHeaders(
                AuthServiceGrpc.newBlockingStub(channel),
                properties.getGrpc().getAppAccessId(),
                properties.getGrpc().getAppSecretKey()
        );

        return stub.registerByEmail(request);
    }

    /**
     * 密码登录
     *
     * @param request protobuf 登录请求
     * @return protobuf 登录响应
     */
    public PasswordLoginResponse passwordLogin(@NonNull PasswordLoginRequest request) {
        log.debug("[gRPC] 执行密码登录: username={}", request.getUsername());

        var stub = GrpcUtil.attachAppHeaders(
                AuthServiceGrpc.newBlockingStub(channel),
                properties.getGrpc().getAppAccessId(),
                properties.getGrpc().getAppSecretKey()
        );

        return stub.passwordLogin(request);
    }

    /**
     * 修改密码
     *
     * @param request protobuf 修改密码请求
     * @return protobuf 修改密码响应
     */
    public ChangePasswordResponse changePassword(@NonNull ChangePasswordRequest request) {
        log.debug("[gRPC] 执行修改密码");

        var stub = GrpcUtil.attachAppHeaders(
                AuthServiceGrpc.newBlockingStub(channel),
                properties.getGrpc().getAppAccessId(),
                properties.getGrpc().getAppSecretKey()
        );

        return stub.changePassword(request);
    }

    /**
     * 撤销令牌
     *
     * @param accessToken Access Token（需要 Bearer 前缀）
     * @param request     protobuf 撤销请求
     * @return protobuf 撤销响应
     */
    public RevokeTokenResponse revokeToken(@NonNull String accessToken, @NonNull RevokeTokenRequest request) {
        log.debug("[gRPC] 执行令牌撤销");

        String normalizedToken = GrpcUtil.normalizeAccessToken(accessToken);
        var stub = GrpcUtil.attachAppHeadersWithToken(
                AuthServiceGrpc.newBlockingStub(channel),
                properties.getGrpc().getAppAccessId(),
                properties.getGrpc().getAppSecretKey(),
                normalizedToken
        );

        return stub.revokeToken(request);
    }
}
