package com.frontleaves.phalanx.beacon.sso.sdk.base.client;

import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthUserinfo;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.GrpcUserConverter;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.User;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * gRPC 用户信息客户端实现
 * <p>
 * 通过 gRPC 请求获取用户信息，委托 {@link SsoRequest} 执行实际的 gRPC 调用，
 * 并通过 {@link GrpcUserConverter} 将 protobuf User 转换为 OAuthUserinfo。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@RequiredArgsConstructor
public class GrpcUserinfoClient implements UserinfoClient {

    private final SsoRequest ssoRequest;
    private final GrpcUserConverter converter;

    @Override
    public Mono<OAuthUserinfo> getUserinfo(String accessToken) {
        return Mono.fromCallable(() -> {
            User grpcUser = ssoRequest.user().getCurrentUser(accessToken);
            return converter.convert(grpcUser);
        });
    }
}
