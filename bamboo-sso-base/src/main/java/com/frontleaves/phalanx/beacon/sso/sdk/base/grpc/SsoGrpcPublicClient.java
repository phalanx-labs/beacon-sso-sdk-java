package com.frontleaves.phalanx.beacon.sso.sdk.base.grpc;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;
import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoGrpcConstants;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoConfigurationException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.GrpcProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.PublicServiceGrpc;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.SendRegisterEmailCodeRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.SendRegisterEmailCodeResponse;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;
import org.springframework.util.StringUtils;

/**
 * SSO gRPC 公共服务客户端
 * <p>
 * 封装 PublicService 的调用逻辑，并自动注入 App 凭证。
 * 无需用户 Token 认证。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
public class SsoGrpcPublicClient {

    private static final Metadata.Key<String> APP_ACCESS_ID_KEY =
            Metadata.Key.of(SsoGrpcConstants.APP_ACCESS_ID_HEADER, Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> APP_SECRET_KEY_KEY =
            Metadata.Key.of(SsoGrpcConstants.APP_SECRET_KEY_HEADER, Metadata.ASCII_STRING_MARSHALLER);

    private final PublicServiceGrpc.PublicServiceBlockingStub publicServiceStub;
    private final String appAccessId;
    private final String appSecretKey;

    /**
     * 构造公共服务客户端
     *
     * @param grpcPublicServiceStub gRPC 公共服务 Stub
     * @param grpcProperties        gRPC 配置属性
     */
    public SsoGrpcPublicClient(PublicServiceGrpc.PublicServiceBlockingStub grpcPublicServiceStub, GrpcProperties grpcProperties) {
        if (grpcPublicServiceStub == null) {
            throw new SsoConfigurationException("gRPC 公共服务 Stub 未配置");
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

        publicServiceStub = grpcPublicServiceStub;
        appAccessId = grpcProperties.getAppAccessId();
        appSecretKey = grpcProperties.getAppSecretKey();
    }

    /**
     * 发送注册邮箱验证码
     *
     * @param request 发送注册邮箱验证码请求
     * @return 发送结果响应
     */
    public SendRegisterEmailCodeResponse sendRegisterEmailCode(SendRegisterEmailCodeRequest request) {
        PublicServiceGrpc.PublicServiceBlockingStub stub = this.attachAppHeaders(publicServiceStub);
        try {
            return stub.sendRegisterEmailCode(request);
        } catch (StatusRuntimeException ex) {
            throw new SsoException(SsoErrorCode.NETWORK_ERROR,
                    "gRPC 公共发送注册邮箱验证码失败: " + ex.getStatus().getDescription(),
                    ex
            );
        }
    }

    private PublicServiceGrpc.PublicServiceBlockingStub attachAppHeaders(
            PublicServiceGrpc.PublicServiceBlockingStub stub
    ) {
        Metadata headers = new Metadata();
        headers.put(APP_ACCESS_ID_KEY, appAccessId);
        headers.put(APP_SECRET_KEY_KEY, appSecretKey);
        return stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(headers));
    }
}
