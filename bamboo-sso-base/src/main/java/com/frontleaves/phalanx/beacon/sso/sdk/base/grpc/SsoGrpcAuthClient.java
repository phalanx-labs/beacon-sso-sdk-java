package com.frontleaves.phalanx.beacon.sso.sdk.base.grpc;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;
import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoGrpcConstants;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoConfigurationException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.TokenException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.GrpcProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.AuthServiceGrpc;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.ChangePasswordRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.ChangePasswordResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.PasswordLoginRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.PasswordLoginResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RegisterByEmailRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RegisterByEmailResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RevokeTokenRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RevokeTokenResponse;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;
import org.springframework.util.StringUtils;

/**
 * SSO gRPC 认证服务客户端
 * <p>
 * 封装 AuthService 的调用逻辑，并自动注入 App 凭证。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
public class SsoGrpcAuthClient {

    private static final Metadata.Key<String> APP_ACCESS_ID_KEY =
            Metadata.Key.of(SsoGrpcConstants.APP_ACCESS_ID_HEADER, Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> APP_SECRET_KEY_KEY =
            Metadata.Key.of(SsoGrpcConstants.APP_SECRET_KEY_HEADER, Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> AUTHORIZATION_KEY =
            Metadata.Key.of(SsoGrpcConstants.AUTHORIZATION_HEADER, Metadata.ASCII_STRING_MARSHALLER);

    private final AuthServiceGrpc.AuthServiceBlockingStub authServiceStub;
    private final String appAccessId;
    private final String appSecretKey;

    public SsoGrpcAuthClient(AuthServiceGrpc.AuthServiceBlockingStub grpcAuthServiceStub, GrpcProperties grpcProperties) {
        if (grpcAuthServiceStub == null) {
            throw new SsoConfigurationException("gRPC 认证服务 Stub 未配置");
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

        authServiceStub = grpcAuthServiceStub;
        appAccessId = grpcProperties.getAppAccessId();
        appSecretKey = grpcProperties.getAppSecretKey();
    }

    public RegisterByEmailResponse registerByEmail(RegisterByEmailRequest request) {
        AuthServiceGrpc.AuthServiceBlockingStub stub = this.attachAppHeaders(authServiceStub);
        try {
            return stub.registerByEmail(request);
        } catch (StatusRuntimeException ex) {
            throw new SsoException(SsoErrorCode.NETWORK_ERROR,
                    "gRPC 认证注册失败: " + ex.getStatus().getDescription(),
                    ex
            );
        }
    }

    public PasswordLoginResponse passwordLogin(PasswordLoginRequest request) {
        AuthServiceGrpc.AuthServiceBlockingStub stub = this.attachAppHeaders(authServiceStub);
        try {
            return stub.passwordLogin(request);
        } catch (StatusRuntimeException ex) {
            throw new SsoException(SsoErrorCode.NETWORK_ERROR,
                    "gRPC 认证登录失败: " + ex.getStatus().getDescription(),
                    ex
            );
        }
    }

    public ChangePasswordResponse changePassword(ChangePasswordRequest request) {
        AuthServiceGrpc.AuthServiceBlockingStub stub = this.attachAppHeaders(authServiceStub);
        try {
            return stub.changePassword(request);
        } catch (StatusRuntimeException ex) {
            throw new SsoException(SsoErrorCode.NETWORK_ERROR,
                    "gRPC 认证修改密码失败: " + ex.getStatus().getDescription(),
                    ex
            );
        }
    }

    /**
     * 注销用户 Token（登出）
     *
     * @param accessToken 用户访问令牌
     * @param request     注销请求
     * @return 注销响应
     */
    public RevokeTokenResponse revokeToken(String accessToken, RevokeTokenRequest request) {
        String token = this.normalizeAccessToken(accessToken);
        AuthServiceGrpc.AuthServiceBlockingStub stub = this.attachAppHeadersWithToken(authServiceStub, token);
        try {
            return stub.revokeToken(request);
        } catch (StatusRuntimeException ex) {
            throw new SsoException(SsoErrorCode.NETWORK_ERROR,
                    "gRPC 认证注销令牌失败: " + ex.getStatus().getDescription(),
                    ex
            );
        }
    }

    private AuthServiceGrpc.AuthServiceBlockingStub attachAppHeaders(
            AuthServiceGrpc.AuthServiceBlockingStub stub
    ) {
        Metadata headers = new Metadata();
        headers.put(APP_ACCESS_ID_KEY, appAccessId);
        headers.put(APP_SECRET_KEY_KEY, appSecretKey);
        return stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(headers));
    }

    /**
     * 附加 App 凭证与用户 Token 到 gRPC 请求头
     *
     * @param stub  gRPC Stub
     * @param token 标准化后的 Bearer Token
     * @return 附加请求头后的 Stub
     */
    private AuthServiceGrpc.AuthServiceBlockingStub attachAppHeadersWithToken(
            AuthServiceGrpc.AuthServiceBlockingStub stub,
            String token
    ) {
        Metadata headers = new Metadata();
        headers.put(APP_ACCESS_ID_KEY, appAccessId);
        headers.put(APP_SECRET_KEY_KEY, appSecretKey);
        headers.put(AUTHORIZATION_KEY, token);
        return stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(headers));
    }

    /**
     * 标准化 Access Token，确保以 Bearer 前缀开头
     *
     * @param accessToken 原始 Access Token
     * @return 标准化后的 Bearer Token
     */
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
