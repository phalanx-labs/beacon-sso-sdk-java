package com.frontleaves.phalanx.beacon.sso.sdk.base.client;

import com.frontleaves.phalanx.beacon.sso.sdk.base.grpc.SsoGrpcAuthClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.grpc.SsoGrpcMerchantClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.grpc.SsoGrpcPublicClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.grpc.SsoGrpcUserClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.logic.AuthLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.base.logic.MerchantLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.base.logic.PublicLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.base.logic.UserLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.GrpcProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.AuthServiceGrpc;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.ChangePasswordRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.ChangePasswordResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.CheckUserHasTagRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.CheckUserHasTagResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetAnnouncementRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetAnnouncementResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetMerchantTagsRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetMerchantTagsResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetRecentAnnouncementsRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetRecentAnnouncementsResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetUserByIDRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetUserTagsRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetUserTagsResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.MerchantServiceGrpc;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.PasswordLoginRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.PasswordLoginResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.PublicServiceGrpc;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RegisterByEmailRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RegisterByEmailResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RevokeTokenRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RevokeTokenResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.SendRegisterEmailCodeRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.SendRegisterEmailCodeResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.UserServiceGrpc;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.User;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * SSO 统一请求门面
 * <p>
 * 对外提供统一的 gRPC 服务访问入口，使用者只需注入 {@code SsoRequest} 即可，
 * 通过 {@code ssoRequest.auth().xxx()} / {@code ssoRequest.user().xxx()} 等方式调用。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
public class SsoRequest {

    private final AuthLogic authLogic;
    private final UserLogic userLogic;
    private final PublicLogic publicLogic;
    private final MerchantLogic merchantLogic;
    private final AuthRequest authRequest;
    private final UserRequest userRequest;
    private final PublicRequest publicRequest;
    private final MerchantRequest merchantRequest;

    public SsoRequest(ManagedChannel channel, GrpcProperties grpcProperties) {
        // 构建 gRPC Stub → Client → Logic
        UserServiceGrpc.UserServiceBlockingStub userStub = UserServiceGrpc.newBlockingStub(channel);
        AuthServiceGrpc.AuthServiceBlockingStub authStub = AuthServiceGrpc.newBlockingStub(channel);
        PublicServiceGrpc.PublicServiceBlockingStub publicStub = PublicServiceGrpc.newBlockingStub(channel);
        MerchantServiceGrpc.MerchantServiceBlockingStub merchantStub = MerchantServiceGrpc.newBlockingStub(channel);

        SsoGrpcUserClient grpcUserClient = new SsoGrpcUserClient(userStub, grpcProperties);
        SsoGrpcAuthClient grpcAuthClient = new SsoGrpcAuthClient(authStub, grpcProperties);
        SsoGrpcPublicClient grpcPublicClient = new SsoGrpcPublicClient(publicStub, grpcProperties);
        SsoGrpcMerchantClient grpcMerchantClient = new SsoGrpcMerchantClient(merchantStub, grpcProperties);

        authLogic = new AuthLogic(grpcAuthClient);
        userLogic = new UserLogic(grpcUserClient);
        publicLogic = new PublicLogic(grpcPublicClient);
        merchantLogic = new MerchantLogic(grpcMerchantClient);

        authRequest = new AuthRequest();
        userRequest = new UserRequest();
        publicRequest = new PublicRequest();
        merchantRequest = new MerchantRequest();

        log.info("SsoRequest 初始化完成 - 认证/用户/公共/商户服务就绪");
    }

    /**
     * 获取认证服务请求器
     *
     * @return 认证请求器
     */
    public AuthRequest auth() {
        return authRequest;
    }

    /**
     * 获取用户服务请求器
     *
     * @return 用户请求器
     */
    public UserRequest user() {
        return userRequest;
    }

    /**
     * 获取公共服务请求器
     *
     * @return 公共请求器
     */
    public PublicRequest publicService() {
        return publicRequest;
    }

    /**
     * 获取商户服务请求器
     *
     * @return 商户请求器
     */
    public MerchantRequest merchant() {
        return merchantRequest;
    }

    // ==================== Auth ====================

    /**
     * 认证服务请求器
     * <p>
     * 提供邮箱注册、密码登录、修改密码、令牌注销等认证相关操作。
     * </p>
     */
    public class AuthRequest {

        /**
         * 邮箱注册
         *
         * @param request 注册请求
         * @return 注册响应
         */
        public RegisterByEmailResponse registerByEmail(RegisterByEmailRequest request) {
            return authLogic.registerByEmail(request);
        }

        /**
         * 密码登录
         *
         * @param request 登录请求
         * @return 登录响应
         */
        public PasswordLoginResponse passwordLogin(PasswordLoginRequest request) {
            return authLogic.passwordLogin(request);
        }

        /**
         * 修改密码
         *
         * @param request 修改密码请求
         * @return 修改密码响应
         */
        public ChangePasswordResponse changePassword(ChangePasswordRequest request) {
            return authLogic.changePassword(request);
        }

        /**
         * 注销用户令牌（登出）
         *
         * @param accessToken 用户访问令牌
         * @param request     注销请求
         * @return 注销响应
         */
        public RevokeTokenResponse revokeToken(String accessToken, RevokeTokenRequest request) {
            return authLogic.revokeToken(accessToken, request);
        }
    }

    // ==================== User ====================

    /**
     * 用户服务请求器
     * <p>
     * 提供获取当前用户信息、按 ID 查询用户等操作。
     * </p>
     */
    public class UserRequest {

        /**
         * 获取当前登录用户信息
         *
         * @param accessToken 用户访问令牌
         * @return 用户信息
         */
        public User getCurrentUser(String accessToken) {
            return userLogic.getCurrentUser(accessToken);
        }

        /**
         * 根据 ID 获取用户信息
         *
         * @param accessToken 用户访问令牌
         * @param request     查询请求
         * @return 用户信息
         */
        public User getUserById(String accessToken, GetUserByIDRequest request) {
            return userLogic.getUserById(accessToken, request);
        }
    }

    // ==================== Public ====================

    /**
     * 公共服务请求器
     * <p>
     * 提供无需用户认证的公开接口操作。
     * </p>
     */
    public class PublicRequest {

        /**
         * 发送注册邮箱验证码
         *
         * @param request 发送验证码请求
         * @return 发送结果
         */
        public SendRegisterEmailCodeResponse sendRegisterEmailCode(SendRegisterEmailCodeRequest request) {
            return publicLogic.sendRegisterEmailCode(request);
        }
    }

    // ==================== Merchant ====================

    /**
     * 商户服务请求器
     * <p>
     * 提供商户标签管理、用户标签查询、公告获取等商户相关操作。
     * </p>
     */
    public class MerchantRequest {

        /**
         * 获取商户标签列表
         *
         * @param request 查询请求
         * @return 商户标签列表
         */
        public GetMerchantTagsResponse getMerchantTags(GetMerchantTagsRequest request) {
            return merchantLogic.getMerchantTags(request);
        }

        /**
         * 获取用户标签列表
         *
         * @param request 查询请求
         * @return 用户标签列表
         */
        public GetUserTagsResponse getUserTags(GetUserTagsRequest request) {
            return merchantLogic.getUserTags(request);
        }

        /**
         * 检查用户是否拥有指定标签
         *
         * @param request 检查请求
         * @return 检查结果
         */
        public CheckUserHasTagResponse checkUserHasTag(CheckUserHasTagRequest request) {
            return merchantLogic.checkUserHasTag(request);
        }

        /**
         * 获取最近公告列表
         *
         * @param request 查询请求
         * @return 最近公告列表
         */
        public GetRecentAnnouncementsResponse getRecentAnnouncements(GetRecentAnnouncementsRequest request) {
            return merchantLogic.getRecentAnnouncements(request);
        }

        /**
         * 获取单个公告详情
         *
         * @param request 查询请求
         * @return 公告详情
         */
        public GetAnnouncementResponse getAnnouncement(GetAnnouncementRequest request) {
            return merchantLogic.getAnnouncement(request);
        }
    }
}
