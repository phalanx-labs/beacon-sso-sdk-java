package com.frontleaves.phalanx.beacon.sso.sdk.base.grpc;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;
import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoGrpcConstants;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoConfigurationException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.GrpcProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.CheckUserHasTagRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.CheckUserHasTagResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetAnnouncementRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetAnnouncementResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetMerchantTagsRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetMerchantTagsResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetRecentAnnouncementsRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetRecentAnnouncementsResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetUserTagsRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetUserTagsResponse;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.MerchantServiceGrpc;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;
import org.springframework.util.StringUtils;

/**
 * SSO gRPC 商户服务客户端
 * <p>
 * 封装 MerchantService 的调用逻辑，并自动注入 App 凭证。
 * 无需用户 Token 认证。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
public class SsoGrpcMerchantClient {

    private static final Metadata.Key<String> APP_ACCESS_ID_KEY =
            Metadata.Key.of(SsoGrpcConstants.APP_ACCESS_ID_HEADER, Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> APP_SECRET_KEY_KEY =
            Metadata.Key.of(SsoGrpcConstants.APP_SECRET_KEY_HEADER, Metadata.ASCII_STRING_MARSHALLER);

    private final MerchantServiceGrpc.MerchantServiceBlockingStub merchantServiceStub;
    private final String appAccessId;
    private final String appSecretKey;

    /**
     * 构造商户服务客户端
     *
     * @param grpcMerchantServiceStub gRPC 商户服务 Stub
     * @param grpcProperties          gRPC 配置属性
     */
    public SsoGrpcMerchantClient(MerchantServiceGrpc.MerchantServiceBlockingStub grpcMerchantServiceStub, GrpcProperties grpcProperties) {
        if (grpcMerchantServiceStub == null) {
            throw new SsoConfigurationException("gRPC 商户服务 Stub 未配置");
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

        merchantServiceStub = grpcMerchantServiceStub;
        appAccessId = grpcProperties.getAppAccessId();
        appSecretKey = grpcProperties.getAppSecretKey();
    }

    /**
     * 获取当前应用所属商户的所有标签
     *
     * @param request 获取商户标签请求
     * @return 商户标签列表响应
     */
    public GetMerchantTagsResponse getMerchantTags(GetMerchantTagsRequest request) {
        MerchantServiceGrpc.MerchantServiceBlockingStub stub = this.attachAppHeaders(merchantServiceStub);
        try {
            return stub.getMerchantTags(request);
        } catch (StatusRuntimeException ex) {
            throw new SsoException(SsoErrorCode.NETWORK_ERROR,
                    "gRPC 商户获取商户标签失败: " + ex.getStatus().getDescription(),
                    ex
            );
        }
    }

    /**
     * 获取指定用户在当前商户的所有标签
     *
     * @param request 获取用户标签请求
     * @return 用户标签列表响应
     */
    public GetUserTagsResponse getUserTags(GetUserTagsRequest request) {
        MerchantServiceGrpc.MerchantServiceBlockingStub stub = this.attachAppHeaders(merchantServiceStub);
        try {
            return stub.getUserTags(request);
        } catch (StatusRuntimeException ex) {
            throw new SsoException(SsoErrorCode.NETWORK_ERROR,
                    "gRPC 商户获取用户标签失败: " + ex.getStatus().getDescription(),
                    ex
            );
        }
    }

    /**
     * 检查用户是否有指定标签
     *
     * @param request 检查用户标签请求
     * @return 检查结果响应
     */
    public CheckUserHasTagResponse checkUserHasTag(CheckUserHasTagRequest request) {
        MerchantServiceGrpc.MerchantServiceBlockingStub stub = this.attachAppHeaders(merchantServiceStub);
        try {
            return stub.checkUserHasTag(request);
        } catch (StatusRuntimeException ex) {
            throw new SsoException(SsoErrorCode.NETWORK_ERROR,
                    "gRPC 商户检查用户标签失败: " + ex.getStatus().getDescription(),
                    ex
            );
        }
    }

    /**
     * 获取最近公告列表
     *
     * @param request 获取最近公告请求
     * @return 最近公告列表响应
     */
    public GetRecentAnnouncementsResponse getRecentAnnouncements(GetRecentAnnouncementsRequest request) {
        MerchantServiceGrpc.MerchantServiceBlockingStub stub = this.attachAppHeaders(merchantServiceStub);
        try {
            return stub.getRecentAnnouncements(request);
        } catch (StatusRuntimeException ex) {
            throw new SsoException(SsoErrorCode.NETWORK_ERROR,
                    "gRPC 商户获取最近公告失败: " + ex.getStatus().getDescription(),
                    ex
            );
        }
    }

    /**
     * 获取单个公告详情
     *
     * @param request 获取公告详情请求
     * @return 公告详情响应
     */
    public GetAnnouncementResponse getAnnouncement(GetAnnouncementRequest request) {
        MerchantServiceGrpc.MerchantServiceBlockingStub stub = this.attachAppHeaders(merchantServiceStub);
        try {
            return stub.getAnnouncement(request);
        } catch (StatusRuntimeException ex) {
            throw new SsoException(SsoErrorCode.NETWORK_ERROR,
                    "gRPC 商户获取公告详情失败: " + ex.getStatus().getDescription(),
                    ex
            );
        }
    }

    private MerchantServiceGrpc.MerchantServiceBlockingStub attachAppHeaders(
            MerchantServiceGrpc.MerchantServiceBlockingStub stub
    ) {
        Metadata headers = new Metadata();
        headers.put(APP_ACCESS_ID_KEY, appAccessId);
        headers.put(APP_SECRET_KEY_KEY, appSecretKey);
        return stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(headers));
    }
}
