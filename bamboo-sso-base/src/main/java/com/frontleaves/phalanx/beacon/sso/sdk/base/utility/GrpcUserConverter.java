package com.frontleaves.phalanx.beacon.sso.sdk.base.utility;

import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.user.UserinfoResult;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.User;

/**
 * gRPC User → UserinfoResult 转换器
 * <p>
 * 将 protobuf {@link User} 消息转换为标准 OIDC {@link UserinfoResult} 模型，
 * 用于 gRPC 用户信息客户端的响应转换。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
public class GrpcUserConverter {

    /**
     * 将 gRPC User 转换为 UserinfoResult
     *
     * @param user gRPC 用户消息
     * @return UserinfoResult 用户信息
     */
    public UserinfoResult convert(User user) {
        return UserinfoResult.builder()
                .sub(user.getId())
                .name(user.getNickname())
                .email(user.hasEmail() ? user.getEmail() : null)
                .emailVerified(user.getIsEmailVerified())
                .picture(user.hasAvatar() ? user.getAvatar() : null)
                .build();
    }
}
