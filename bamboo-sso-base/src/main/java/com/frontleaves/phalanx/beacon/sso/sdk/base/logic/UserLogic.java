package com.frontleaves.phalanx.beacon.sso.sdk.base.logic;

import com.frontleaves.phalanx.beacon.sso.sdk.base.grpc.SsoGrpcUserClient;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.User;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户业务逻辑组件
 * <p>
 * 通过 gRPC 用户服务获取当前登录用户的详细信息。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
public class UserLogic {

    private final SsoGrpcUserClient ssoGrpcUserClient;

    public UserLogic(SsoGrpcUserClient grpcUserClient) {
        ssoGrpcUserClient = grpcUserClient;
    }

    /**
     * 获取当前登录用户信息
     *
     * @param accessToken 用户访问令牌
     * @return 用户信息
     */
    public User getCurrentUser(String accessToken) {
        log.info("GetCurrentUser - 获取当前用户信息");
        return ssoGrpcUserClient.getCurrentUser(accessToken);
    }
}
