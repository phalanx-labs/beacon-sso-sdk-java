package com.frontleaves.phalanx.beacon.sso.sdk.base.utility;

import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthUserinfo;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.User;

/**
 * gRPC User → OAuthUserinfo 转换器
 * <p>
 * 将 protobuf {@link User} 消息转换为标准 OIDC {@link OAuthUserinfo} 模型，
 * 用于 gRPC 用户信息客户端的响应转换。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
public class GrpcUserConverter {

    /**
     * 将 gRPC User 转换为 OAuthUserinfo
     *
     * @param user gRPC 用户消息
     * @return OAuthUserinfo 用户信息
     */
    public OAuthUserinfo convert(User user) {
        return OAuthUserinfo.builder()
                .sub(user.getId())
                .name(user.getNickname())
                .preferredUsername(user.getUsername())
                .email(user.getEmail())
                .emailVerified(user.getIsEmailVerified())
                .picture(user.getAvatar())
                .build();
    }
}
