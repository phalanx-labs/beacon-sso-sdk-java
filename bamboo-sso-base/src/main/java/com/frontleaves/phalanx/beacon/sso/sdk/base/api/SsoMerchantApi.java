package com.frontleaves.phalanx.beacon.sso.sdk.base.api;

import com.frontleaves.phalanx.beacon.sso.sdk.base.api.grpc.SsoGrpcMerchantClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.merchant.CheckUserHasTagRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.merchant.GetAnnouncementRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.merchant.GetMerchantTagsRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.merchant.GetRecentAnnouncementsRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.merchant.GetUserTagsRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.merchant.AnnouncementResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.merchant.MerchantTagResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.merchant.RecentAnnouncementsResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.GrpcModelConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 商户操作聚合层（gRPC-only）
 * <p>
 * 封装商户相关操作，包括标签管理和公告获取。
 * 负责将 SDK Request 转换为 protobuf Request，调用 gRPC 客户端，
 * 并将 protobuf Response 转换为 SDK Result。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RequiredArgsConstructor
public class SsoMerchantApi {

    private final SsoGrpcMerchantClient grpcClient;
    private final GrpcModelConverter converter;

    /**
     * 获取商户标签列表
     *
     * @param request 标签查询请求
     * @return 商户标签列表
     */
    public List<MerchantTagResult> getMerchantTags(GetMerchantTagsRequest request) {
        log.debug("[聚合层] 获取商户标签列表");

        // SDK Request → Protobuf Request
        // 注意：protobuf GetMerchantTagsRequest 只有 enabledOnly 字段
        var grpcRequestBuilder = com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetMerchantTagsRequest.newBuilder();
        // merchantId 在 protobuf 中不存在，商户信息从 App 凭证中获取

        // 调用 gRPC 客户端
        var response = grpcClient.getMerchantTags(grpcRequestBuilder.build());

        // Protobuf Response → SDK Result
        return converter.toMerchantTags(response.getTagsList());
    }

    /**
     * 获取用户标签列表
     *
     * @param request 用户标签查询请求
     * @return 用户标签列表
     */
    public List<MerchantTagResult> getUserTags(GetUserTagsRequest request) {
        log.debug("[聚合层] 获取用户标签列表: userId={}", request.getUserId());

        // SDK Request → Protobuf Request
        var grpcRequest = com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetUserTagsRequest.newBuilder()
                .setUserId(request.getUserId())
                .build();

        // 调用 gRPC 客户端
        var response = grpcClient.getUserTags(grpcRequest);

        // Protobuf Response → SDK Result
        return converter.toMerchantTags(response.getTagsList());
    }

    /**
     * 检查用户是否拥有指定标签
     *
     * @param request 标签检查请求
     * @return 是否拥有该标签
     */
    public boolean checkUserHasTag(CheckUserHasTagRequest request) {
        log.debug("[聚合层] 检查用户标签: userId={}, tagCode={}", request.getUserId(), request.getTagCode());

        // SDK Request → Protobuf Request
        var grpcRequest = com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.CheckUserHasTagRequest.newBuilder()
                .setUserId(request.getUserId())
                .setTagCode(request.getTagCode())
                .build();

        // 调用 gRPC 客户端
        var response = grpcClient.checkUserHasTag(grpcRequest);

        return response.getHasTag();
    }

    /**
     * 获取最近公告列表
     *
     * @param request 公告查询请求
     * @return 最近公告响应
     */
    public RecentAnnouncementsResult getRecentAnnouncements(GetRecentAnnouncementsRequest request) {
        log.debug("[聚合层] 获取最近公告列表");

        // SDK Request → Protobuf Request
        var grpcRequestBuilder = com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetRecentAnnouncementsRequest.newBuilder();
        if (request.getLimit() != null) {
            grpcRequestBuilder.setLimit(request.getLimit());
        }

        // 调用 gRPC 客户端
        var response = grpcClient.getRecentAnnouncements(grpcRequestBuilder.build());

        // Protobuf Response → SDK Result
        return converter.toRecentAnnouncementsResult(response);
    }

    /**
     * 获取单个公告详情
     *
     * @param request 公告查询请求
     * @return 公告详情
     */
    public AnnouncementResult getAnnouncement(GetAnnouncementRequest request) {
        log.debug("[聚合层] 获取公告详情: announcementId={}", request.getAnnouncementId());

        // SDK Request → Protobuf Request
        var grpcRequest = com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetAnnouncementRequest.newBuilder()
                .setAnnouncementId(request.getAnnouncementId())
                .build();

        // 调用 gRPC 客户端
        var response = grpcClient.getAnnouncement(grpcRequest);

        // Protobuf Response → SDK Result
        return converter.toAnnouncement(response.getAnnouncement());
    }
}
