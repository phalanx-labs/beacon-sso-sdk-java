package com.frontleaves.phalanx.beacon.sso.sdk.base.api;

import com.frontleaves.phalanx.beacon.sso.sdk.base.api.grpc.SsoGrpcUserClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.api.http.SsoHttpUserClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoConfigurationException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.user.GetUserByIdRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.common.RoleResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.user.UserDetailResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.user.UserinfoResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 用户操作聚合层（双传输：gRPC 优先，HTTP 回退）
 * <p>
 * 封装用户相关操作，支持 gRPC 和 HTTP 双传输。
 * 当 gRPC 启用时优先使用 {@link SsoGrpcUserClient}，否则回退到 {@link SsoHttpUserClient}。
 * 负责将 SDK Request 转换为 protobuf Request，调用 gRPC 客户端，
 * 并将 protobuf Response 转换为 SDK Result。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RequiredArgsConstructor
public class SsoUserApi {

    private final BeaconSsoProperties properties;
    private final SsoGrpcUserClient grpcClient;
    private final SsoHttpUserClient httpClient;

    /**
     * 获取当前用户信息（双传输：gRPC 优先，HTTP 回退）
     *
     * @param accessToken 访问令牌
     * @return UserinfoResult 用户信息
     */
    public Mono<UserinfoResult> getCurrentUser(String accessToken) {
        if (isGrpcEnabled()) {
            log.debug("[聚合层] 使用 gRPC 获取当前用户信息");

            // Protobuf Request
            var grpcRequest = com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetCurrentUserRequest.newBuilder()
                    .build();

            // 调用 gRPC 客户端并转换响应
            return Mono.fromCallable(() -> {
                var response = grpcClient.getCurrentUser(accessToken, grpcRequest);
                var user = response.getUser();
                return UserinfoResult.builder()
                        .sub(user.getId())
                        .name(user.getNickname())
                        .email(user.hasEmail() ? user.getEmail() : null)
                        .emailVerified(user.getIsEmailVerified())
                        .picture(user.hasAvatar() ? user.getAvatar() : null)
                        .build();
            });
        }
        log.debug("[聚合层] 使用 HTTP 获取当前用户信息");
        return httpClient.getUserinfoSdk(accessToken);
    }

    /**
     * 根据用户 ID 获取详细信息（gRPC-only）
     * <p>
     * 此方法仅支持 gRPC 通信，未启用 gRPC 时将抛出异常。
     * </p>
     *
     * @param accessToken 访问令牌
     * @param request     用户查询请求
     * @return UserDetailResult 用户详细信息
     * @throws SsoConfigurationException 如果 gRPC 未启用
     */
    public UserDetailResult getUserById(String accessToken, GetUserByIdRequest request) {
        if (!isGrpcEnabled()) {
            throw new SsoConfigurationException(
                    SsoErrorCode.GRPC_NOT_ENABLED,
                    "getUserById 需要 gRPC 通信，请启用 gRPC 配置"
            );
        }
        log.debug("[聚合层] 使用 gRPC 根据 ID 获取用户信息: userId={}", request.getUserId());

        // SDK Request → Protobuf Request
        var grpcRequest = com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetUserByIDRequest.newBuilder()
                .setUserId(request.getUserId())
                .build();

        // 调用 gRPC 客户端
        var response = grpcClient.getUserByID(accessToken, grpcRequest);
        var user = response.getUser();

        // Protobuf Response → SDK Result
        return UserDetailResult.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.hasEmail() ? user.getEmail() : null)
                .emailVerified(user.getIsEmailVerified())
                .avatar(user.hasAvatar() ? user.getAvatar() : null)
                .phone(user.hasPhone() ? user.getPhone() : null)
                .phoneVerified(user.getIsPhoneVerified())
                .gender(user.getGender())
                .birthday(user.hasBirthday() ? user.getBirthday() : null)
                .status(user.getStatus())
                .needResetPassword(user.getNeedResetPassword())
                .lastLoginAt(user.hasLastLoginAt() ? user.getLastLoginAt() : null)
                .lastLoginIp(user.hasLastLoginIp() ? user.getLastLoginIp() : null)
                .roles(user.getRolesList().stream().map(role -> RoleResult.builder()
                        .code(role.getCode())
                        .name(role.getName())
                        .description(role.hasDescription() ? role.getDescription() : null)
                        .build()).toList()
                ).build();
    }

    /**
     * 判断 gRPC 是否启用
     *
     * @return 如果 gRPC 启用返回 true，否则返回 false
     */
    private boolean isGrpcEnabled() {
        return properties.getGrpc() != null && properties.getGrpc().isEnabled();
    }
}
