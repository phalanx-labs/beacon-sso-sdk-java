package com.frontleaves.phalanx.beacon.sso.sdk.base.api;

import com.frontleaves.phalanx.beacon.sso.sdk.base.api.grpc.SsoGrpcAuthClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoConfigurationException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.account.ChangePasswordRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.account.PasswordLoginRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.account.RegisterEmailRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.account.RevokeTokenRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.account.LoginResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.account.RegisterResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RegisterByEmailRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

/**
 * 账户管理聚合层（gRPC-only）
 * <p>
 * 封装账户管理相关操作，包括邮箱注册、密码登录、修改密码和令牌撤销。
 * 负责将 SDK Request 转换为 protobuf Request，调用 gRPC 客户端，
 * 并将 protobuf Response 转换为 SDK Result。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
public class SsoAccountApi {

    private final BeaconSsoProperties properties;
    private final SsoGrpcAuthClient grpcClient;

    public SsoAccountApi(BeaconSsoProperties properties, SsoGrpcAuthClient grpcClient) {
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
     * 通过邮箱注册新用户
     *
     * @param request 注册请求
     * @return 注册结果
     */
    public RegisterResult registerByEmail(@NotNull RegisterEmailRequest request) {
        if (!isGrpcEnabled()) {
            throw new SsoConfigurationException(
                    SsoErrorCode.GRPC_NOT_ENABLED,
                    "registerByEmail 需要 gRPC 通信，请启用 gRPC 配置"
            );
        }

        log.debug("[聚合层] 执行邮箱注册: email={}", request.getEmail());

        // SDK Request → Protobuf Request
        var grpcRequestBuilder = RegisterByEmailRequest.newBuilder()
                .setEmail(request.getEmail())
                .setPassword(request.getPassword())
                .setUsername(request.getUsername());
        if (StringUtils.hasText(request.getCode())) {
            grpcRequestBuilder.setCode(request.getCode());
        }
        if (StringUtils.hasText(request.getNickname())) {
            grpcRequestBuilder.setNickname(request.getNickname());
        }

        // 调用 gRPC 客户端
        var response = grpcClient.registerByEmail(grpcRequestBuilder.build());

        // Protobuf Response → SDK Result
        return RegisterResult.builder()
                .userId(response.getUserId())
                .token(response.getToken())
                .build();
    }

    /**
     * 通过用户名和密码登录
     *
     * @param request 登录请求
     * @return 登录结果
     */
    public LoginResult passwordLogin(PasswordLoginRequest request) {
        if (!isGrpcEnabled()) {
            throw new SsoConfigurationException(
                    SsoErrorCode.GRPC_NOT_ENABLED,
                    "passwordLogin 需要 gRPC 通信，请启用 gRPC 配置"
            );
        }

        log.debug("[聚合层] 执行密码登录: username={}", request.getUsername());

        // SDK Request → Protobuf Request
        var grpcRequestBuilder = com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.PasswordLoginRequest.newBuilder()
                .setUsername(request.getUsername())
                .setPassword(request.getPassword());

        // 添加可选字段
        if (StringUtils.hasText(request.getScope())) {
            grpcRequestBuilder.setScope(request.getScope());
        }
        if (StringUtils.hasText(request.getClientIp())) {
            grpcRequestBuilder.setClientIp(request.getClientIp());
        }
        if (StringUtils.hasText(request.getUserAgent())) {
            grpcRequestBuilder.setUserAgent(request.getUserAgent());
        }

        // 调用 gRPC 客户端
        var response = grpcClient.passwordLogin(grpcRequestBuilder.build());

        // Protobuf Response → SDK Result
        return LoginResult.builder()
                .accessToken(response.getAccessToken())
                .tokenType(response.getTokenType())
                .expiresIn(response.getExpiresIn())
                .refreshToken(response.hasRefreshToken() ? response.getRefreshToken() : null)
                .scope(response.hasScope() ? response.getScope() : null)
                .idToken(response.hasIdToken() ? response.getIdToken() : null)
                .build();
    }

    /**
     * 修改用户密码
     *
     * @param request 修改密码请求
     */
    public void changePassword(ChangePasswordRequest request) {
        if (!isGrpcEnabled()) {
            throw new SsoConfigurationException(
                    SsoErrorCode.GRPC_NOT_ENABLED,
                    "changePassword 需要 gRPC 通信，请启用 gRPC 配置"
            );
        }

        log.debug("[聚合层] 执行修改密码: userId={}", request.getUserId());

        // SDK Request → Protobuf Request
        var grpcRequestBuilder = com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.ChangePasswordRequest.newBuilder()
                .setUserId(request.getUserId())
                .setNewPassword(request.getNewPassword());

        if (StringUtils.hasText(request.getOldPassword())) {
            grpcRequestBuilder.setOldPassword(request.getOldPassword());
        }

        // 调用 gRPC 客户端
        grpcClient.changePassword(grpcRequestBuilder.build());
    }

    /**
     * 撤销用户令牌
     *
     * @param accessToken Access Token
     * @param request     撤销请求
     */
    public void revokeToken(String accessToken, RevokeTokenRequest request) {
        if (!isGrpcEnabled()) {
            throw new SsoConfigurationException(
                    SsoErrorCode.GRPC_NOT_ENABLED,
                    "revokeToken 需要 gRPC 通信，请启用 gRPC 配置"
            );
        }

        log.debug("[聚合层] 执行令牌撤销");

        // SDK Request → Protobuf Request
        // 注意：protobuf RevokeTokenRequest 只有 tokenTypeHint 字段，token 从 metadata 传递
        var grpcRequestBuilder = com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RevokeTokenRequest.newBuilder();
        if (StringUtils.hasText(request.getTokenTypeHint())) {
            grpcRequestBuilder.setTokenTypeHint(request.getTokenTypeHint());
        }

        // 调用 gRPC 客户端
        grpcClient.revokeToken(accessToken, grpcRequestBuilder.build());
    }
}
