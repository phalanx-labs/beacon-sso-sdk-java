package com.frontleaves.phalanx.beacon.sso.sdk.base.grpc;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;
import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoGrpcConstants;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoConfigurationException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.GrpcProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.AuthServiceGrpc;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.ChangePasswordRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.ChangePasswordResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.PasswordLoginRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.PasswordLoginResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RegisterByEmailRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.RegisterByEmailResponse;
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

    private final AuthServiceGrpc.AuthServiceBlockingStub authServiceStub;
    private final String appAccessId;
    private final String appSecretKey;

    public SsoGrpcAuthClient(AuthServiceGrpc.AuthServiceBlockingStub grpcAuthServiceStub, GrpcProperties grpcProperties) {
        if (grpcAuthServiceStub == null) {
            throw new SsoConfigurationException("gRPC AuthService stub is not configured");
        }
        if (grpcProperties == null) {
            throw new SsoConfigurationException("gRPC properties are not configured");
        }
        if (!StringUtils.hasText(grpcProperties.getAppAccessId())) {
            throw new SsoConfigurationException("gRPC app access id is not configured");
        }
        if (!StringUtils.hasText(grpcProperties.getAppSecretKey())) {
            throw new SsoConfigurationException("gRPC app secret key is not configured");
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
                    "gRPC auth register failed: " + ex.getStatus().getDescription(),
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
                    "gRPC auth login failed: " + ex.getStatus().getDescription(),
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
                    "gRPC auth change password failed: " + ex.getStatus().getDescription(),
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
}
