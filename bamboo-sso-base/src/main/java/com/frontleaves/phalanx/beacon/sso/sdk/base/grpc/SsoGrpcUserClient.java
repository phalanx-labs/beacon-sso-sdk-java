package com.frontleaves.phalanx.beacon.sso.sdk.base.grpc;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;
import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoGrpcConstants;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoConfigurationException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.TokenException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.GrpcProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetCurrentUserRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetCurrentUserResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetUserByIDRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetUserByIDResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.User;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.UserServiceGrpc;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;
import org.springframework.util.StringUtils;

/**
 * SSO gRPC 用户服务客户端
 * <p>
 * 封装 UserService 的调用逻辑，并自动注入 App 凭证与用户 Token。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
public class SsoGrpcUserClient {

    private static final Metadata.Key<String> APP_ACCESS_ID_KEY =
            Metadata.Key.of(SsoGrpcConstants.APP_ACCESS_ID_HEADER, Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> APP_SECRET_KEY_KEY =
            Metadata.Key.of(SsoGrpcConstants.APP_SECRET_KEY_HEADER, Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> AUTHORIZATION_KEY =
            Metadata.Key.of(SsoGrpcConstants.AUTHORIZATION_HEADER, Metadata.ASCII_STRING_MARSHALLER);

    private final UserServiceGrpc.UserServiceBlockingStub userServiceStub;
    private final String appAccessId;
    private final String appSecretKey;

    public SsoGrpcUserClient(UserServiceGrpc.UserServiceBlockingStub grpcUserServiceStub, GrpcProperties grpcProperties) {
        if (grpcUserServiceStub == null) {
            throw new SsoConfigurationException("gRPC 用户服务 Stub 未配置");
        }
        if (grpcProperties == null) {
            throw new SsoConfigurationException("gRPC 配置未设置");
        }
        if (!StringUtils.hasText(grpcProperties.getAppAccessId())) {
            throw new SsoConfigurationException("gRPC 应用访问 ID 未配置");
        }
        if (!StringUtils.hasText(grpcProperties.getAppSecretKey())) {
            throw new SsoConfigurationException("gRPC 应用密钥未配置");
        }

        userServiceStub = grpcUserServiceStub;
        appAccessId = grpcProperties.getAppAccessId();
        appSecretKey = grpcProperties.getAppSecretKey();
    }

    /**
     * 获取当前用户信息
     *
     * @param accessToken 用户访问令牌
     * @return 用户信息
     */
    public User getCurrentUser(String accessToken) {
        String token = this.normalizeAccessToken(accessToken);
        Metadata headers = new Metadata();
        headers.put(APP_ACCESS_ID_KEY, appAccessId);
        headers.put(APP_SECRET_KEY_KEY, appSecretKey);
        headers.put(AUTHORIZATION_KEY, token);

        UserServiceGrpc.UserServiceBlockingStub stub = userServiceStub
                .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(headers));
        GetCurrentUserResponse response;
        try {
            response = stub.getCurrentUser(GetCurrentUserRequest.newBuilder().build());
        } catch (StatusRuntimeException ex) {
            throw TokenException.accessTokenError(
                    SsoErrorCode.TOKEN_INVALID,
                    "gRPC 用户服务调用失败: " + ex.getStatus().getDescription()
            );
        }

        if (response == null || !response.hasUser()) {
            throw TokenException.accessTokenError(SsoErrorCode.USERINFO_FAILED, "用户信息响应为空");
        }

        return response.getUser();
    }

    /**
     * 根据 ID 获取用户信息
     *
     * @param accessToken 用户访问令牌
     * @param request     查询请求（包含用户 ID）
     * @return 用户信息
     */
    public User getUserById(String accessToken, GetUserByIDRequest request) {
        String token = this.normalizeAccessToken(accessToken);
        Metadata headers = new Metadata();
        headers.put(APP_ACCESS_ID_KEY, appAccessId);
        headers.put(APP_SECRET_KEY_KEY, appSecretKey);
        headers.put(AUTHORIZATION_KEY, token);

        UserServiceGrpc.UserServiceBlockingStub stub = userServiceStub
                .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(headers));
        GetUserByIDResponse response;
        try {
            response = stub.getUserByID(request);
        } catch (StatusRuntimeException ex) {
            throw TokenException.accessTokenError(
                    SsoErrorCode.TOKEN_INVALID,
                    "gRPC 按 ID 获取用户调用失败: " + ex.getStatus().getDescription()
            );
        }

        if (response == null || !response.hasUser()) {
            throw TokenException.accessTokenError(SsoErrorCode.USERINFO_FAILED, "按 ID 获取用户响应为空");
        }

        return response.getUser();
    }

    private String normalizeAccessToken(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            throw TokenException.accessTokenError(SsoErrorCode.TOKEN_INVALID, "Access Token 不能为空");
        }

        String token = accessToken.trim();
        if (token.regionMatches(true, 0, SsoGrpcConstants.BEARER_PREFIX, 0, SsoGrpcConstants.BEARER_PREFIX.length())) {
            token = SsoGrpcConstants.BEARER_PREFIX + token.substring(SsoGrpcConstants.BEARER_PREFIX.length()).trim();
        } else {
            token = SsoGrpcConstants.BEARER_PREFIX + token;
        }
        return token;
    }
}
