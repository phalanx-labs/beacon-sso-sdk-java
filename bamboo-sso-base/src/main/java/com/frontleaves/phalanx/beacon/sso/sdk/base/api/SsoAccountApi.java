package com.frontleaves.phalanx.beacon.sso.sdk.base.api;

import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.SsoLoginResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.SsoRegisterResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.GrpcModelConverter;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.ChangePasswordRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.PasswordLoginRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RegisterByEmailRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RevokeTokenRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 账户管理（gRPC-only）
 * <p>
 * 封装账户管理相关操作，包括邮箱注册、密码登录、修改密码和令牌撤销。
 * 所有方法均通过 gRPC 协议与 SSO 服务通信。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RequiredArgsConstructor
public class SsoAccountApi {

    private final SsoRequest ssoRequest;
    private final GrpcModelConverter converter;

    /**
     * 通过邮箱注册新用户
     *
     * @param request 注册请求
     * @return 注册结果
     */
    public SsoRegisterResult registerByEmail(RegisterByEmailRequest request) {
        log.debug("执行邮箱注册: email={}", request.getEmail());
        return converter.toRegisterResult(ssoRequest.auth().registerByEmail(request));
    }

    /**
     * 通过用户名和密码登录
     *
     * @param request 登录请求
     * @return 登录结果
     */
    public SsoLoginResult passwordLogin(PasswordLoginRequest request) {
        log.debug("执行密码登录: username={}", request.getUsername());
        return converter.toLoginResult(ssoRequest.auth().passwordLogin(request));
    }

    /**
     * 修改用户密码
     *
     * @param request 修改密码请求
     */
    public void changePassword(ChangePasswordRequest request) {
        log.debug("执行修改密码");
        ssoRequest.auth().changePassword(request);
    }

    /**
     * 撤销用户令牌
     *
     * @param accessToken Access Token
     * @param request     撤销请求
     */
    public void revokeToken(String accessToken, RevokeTokenRequest request) {
        log.debug("执行令牌撤销");
        ssoRequest.auth().revokeToken(accessToken, request);
    }
}
