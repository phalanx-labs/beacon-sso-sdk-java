package com.frontleaves.phalanx.beacon.sso.sdk.base.api.grpc;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoCacheConstants;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.GrpcUtil;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetCurrentUserRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetCurrentUserResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetUserByIDRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetUserByIDResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.UserServiceGrpc;
import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.annotation.Cacheable;

/**
 * SSO gRPC 用户服务客户端
 * <p>
 * 封装用户相关的 gRPC 调用，直接使用 protobuf 原生类型。
 * 所有方法的形参和返回值均为 protobuf 生成的类型。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RequiredArgsConstructor
public class SsoGrpcUserClient {

    private final BeaconSsoProperties properties;
    private final ManagedChannel channel;

    /**
     * 获取当前用户信息
     */
    @Cacheable(cacheNames = SsoCacheConstants.CACHE_GRPC_USERINFO, key = "#accessToken")
    public GetCurrentUserResponse getCurrentUser(@NonNull String accessToken, @NonNull GetCurrentUserRequest request) {
        log.debug("[gRPC] 获取当前用户信息");

        String normalizedToken = GrpcUtil.normalizeAccessToken(accessToken);
        var stub = GrpcUtil.attachAppHeadersWithToken(
                UserServiceGrpc.newBlockingStub(channel),
                properties.getGrpc().getAppAccessId(),
                properties.getGrpc().getAppSecretKey(),
                normalizedToken
        );

        return stub.getCurrentUser(request);
    }

    /**
     * 根据用户 ID 获取详细信息
     */
    @Cacheable(cacheNames = SsoCacheConstants.CACHE_GRPC_USERINFO, key = "#request.userId")
    public GetUserByIDResponse getUserByID(@NonNull String accessToken, @NonNull GetUserByIDRequest request) {
        log.debug("[gRPC] 根据 ID 获取用户信息: userId={}", request.getUserId());

        String normalizedToken = GrpcUtil.normalizeAccessToken(accessToken);
        var stub = GrpcUtil.attachAppHeadersWithToken(
                UserServiceGrpc.newBlockingStub(channel),
                properties.getGrpc().getAppAccessId(),
                properties.getGrpc().getAppSecretKey(),
                normalizedToken
        );

        return stub.getUserByID(request);
    }
}
