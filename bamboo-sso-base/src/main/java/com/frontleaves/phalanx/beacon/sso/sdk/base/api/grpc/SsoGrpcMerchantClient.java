package com.frontleaves.phalanx.beacon.sso.sdk.base.api.grpc;

import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.GrpcUtil;
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
import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

/**
 * SSO gRPC 商户服务客户端
 * <p>
 * 封装商户相关的 gRPC 调用，直接使用 protobuf 原生类型。
 * 所有方法的形参和返回值均为 protobuf 生成的类型。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RequiredArgsConstructor
public class SsoGrpcMerchantClient {

    private final BeaconSsoProperties properties;
    private final ManagedChannel channel;

    /**
     * 获取商户标签列表
     *
     * @param request protobuf 标签查询请求
     * @return protobuf 标签列表响应
     */
    public GetMerchantTagsResponse getMerchantTags(@NonNull GetMerchantTagsRequest request) {
        log.debug("[gRPC] 获取商户标签列表");

        var stub = GrpcUtil.attachAppHeaders(
                MerchantServiceGrpc.newBlockingStub(channel),
                properties.getGrpc().getAppAccessId(),
                properties.getGrpc().getAppSecretKey()
        );

        return stub.getMerchantTags(request);
    }

    /**
     * 获取用户标签列表
     *
     * @param request protobuf 用户标签查询请求
     * @return protobuf 用户标签列表响应
     */
    public GetUserTagsResponse getUserTags(@NonNull GetUserTagsRequest request) {
        log.debug("[gRPC] 获取用户标签列表: userId={}", request.getUserId());

        var stub = GrpcUtil.attachAppHeaders(
                MerchantServiceGrpc.newBlockingStub(channel),
                properties.getGrpc().getAppAccessId(),
                properties.getGrpc().getAppSecretKey()
        );

        return stub.getUserTags(request);
    }

    /**
     * 检查用户是否拥有指定标签
     *
     * @param request protobuf 标签检查请求
     * @return protobuf 检查结果响应
     */
    public CheckUserHasTagResponse checkUserHasTag(@NonNull CheckUserHasTagRequest request) {
        log.debug("[gRPC] 检查用户标签: userId={}, tagCode={}", request.getUserId(), request.getTagCode());

        var stub = GrpcUtil.attachAppHeaders(
                MerchantServiceGrpc.newBlockingStub(channel),
                properties.getGrpc().getAppAccessId(),
                properties.getGrpc().getAppSecretKey()
        );

        return stub.checkUserHasTag(request);
    }

    /**
     * 获取最近公告列表
     *
     * @param request protobuf 公告查询请求
     * @return protobuf 公告列表响应
     */
    public GetRecentAnnouncementsResponse getRecentAnnouncements(@NonNull GetRecentAnnouncementsRequest request) {
        log.debug("[gRPC] 获取最近公告列表");

        var stub = GrpcUtil.attachAppHeaders(
                MerchantServiceGrpc.newBlockingStub(channel),
                properties.getGrpc().getAppAccessId(),
                properties.getGrpc().getAppSecretKey()
        );

        return stub.getRecentAnnouncements(request);
    }

    /**
     * 获取单个公告详情
     *
     * @param request protobuf 公告查询请求
     * @return protobuf 公告详情响应
     */
    public GetAnnouncementResponse getAnnouncement(@NonNull GetAnnouncementRequest request) {
        log.debug("[gRPC] 获取公告详情: announcementId={}", request.getAnnouncementId());

        var stub = GrpcUtil.attachAppHeaders(
                MerchantServiceGrpc.newBlockingStub(channel),
                properties.getGrpc().getAppAccessId(),
                properties.getGrpc().getAppSecretKey()
        );

        return stub.getAnnouncement(request);
    }
}
