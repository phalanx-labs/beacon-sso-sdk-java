package com.frontleaves.phalanx.beacon.sso.sdk.base.api;

import com.frontleaves.phalanx.beacon.sso.sdk.base.api.grpc.SsoGrpcMerchantClient;
import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoConfigurationException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.merchant.CheckUserHasTagRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.merchant.GetAnnouncementRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.merchant.GetMerchantTagsRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.merchant.GetRecentAnnouncementsRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.merchant.GetUserTagsRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.merchant.AnnouncementListMetaResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.merchant.AnnouncementResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.merchant.MerchantTagResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.merchant.RecentAnnouncementsResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
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
public class SsoMerchantApi {

    private final BeaconSsoProperties properties;
    private final SsoGrpcMerchantClient grpcClient;

    public SsoMerchantApi(BeaconSsoProperties properties, SsoGrpcMerchantClient grpcClient) {
        this.properties = properties;
        this.grpcClient = grpcClient;
    }

    /**
     * 检查 gRPC 是否启用
     */
    private boolean isGrpcEnabled() {
        return properties.getGrpc() != null && properties.getGrpc().isEnabled();
    }

    /**
     * 获取商户标签列表
     *
     * @param request 标签查询请求
     * @return 商户标签列表
     */
    public List<MerchantTagResult> getMerchantTags(GetMerchantTagsRequest request) {
        if (!isGrpcEnabled()) {
            throw new SsoConfigurationException(
                    SsoErrorCode.GRPC_NOT_ENABLED,
                    "getMerchantTags 需要 gRPC 通信，请启用 gRPC 配置"
            );
        }

        log.debug("[聚合层] 获取商户标签列表");

        // SDK Request → Protobuf Request
        var grpcRequestBuilder = com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetMerchantTagsRequest.newBuilder();

        // 添加 enabledOnly 字段
        if (request.getEnabledOnly() != null) {
            grpcRequestBuilder.setEnabledOnly(request.getEnabledOnly());
        }

        // 调用 gRPC 客户端
        var response = grpcClient.getMerchantTags(grpcRequestBuilder.build());

        // Protobuf Response → SDK Result
        return toMerchantTags(response.getTagsList());
    }

    /**
     * 获取用户标签列表
     *
     * @param request 用户标签查询请求
     * @return 用户标签列表
     */
    public List<MerchantTagResult> getUserTags(GetUserTagsRequest request) {
        if (!isGrpcEnabled()) {
            throw new SsoConfigurationException(
                    SsoErrorCode.GRPC_NOT_ENABLED,
                    "getUserTags 需要 gRPC 通信，请启用 gRPC 配置"
            );
        }

        log.debug("[聚合层] 获取用户标签列表: userId={}", request.getUserId());

        // SDK Request → Protobuf Request
        var grpcRequest = com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetUserTagsRequest.newBuilder()
                .setUserId(request.getUserId())
                .build();

        // 调用 gRPC 客户端
        var response = grpcClient.getUserTags(grpcRequest);

        // Protobuf Response → SDK Result
        return toMerchantTags(response.getTagsList());
    }

    /**
     * 检查用户是否拥有指定标签
     *
     * @param request 标签检查请求
     * @return 是否拥有该标签
     */
    public boolean checkUserHasTag(CheckUserHasTagRequest request) {
        if (!isGrpcEnabled()) {
            throw new SsoConfigurationException(
                    SsoErrorCode.GRPC_NOT_ENABLED,
                    "checkUserHasTag 需要 gRPC 通信，请启用 gRPC 配置"
            );
        }

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
        if (!isGrpcEnabled()) {
            throw new SsoConfigurationException(
                    SsoErrorCode.GRPC_NOT_ENABLED,
                    "getRecentAnnouncements 需要 gRPC 通信，请启用 gRPC 配置"
            );
        }

        log.debug("[聚合层] 获取最近公告列表");

        // SDK Request → Protobuf Request
        var grpcRequestBuilder = com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetRecentAnnouncementsRequest.newBuilder();

        if (request.getLimit() != null) {
            grpcRequestBuilder.setLimit(request.getLimit());
        }
        if (request.getActiveOnly() != null) {
            grpcRequestBuilder.setActiveOnly(request.getActiveOnly());
        }

        // 调用 gRPC 客户端
        var response = grpcClient.getRecentAnnouncements(grpcRequestBuilder.build());

        // Protobuf Response → SDK Result
        return RecentAnnouncementsResult.builder()
                .announcements(response.getAnnouncementsList().stream()
                        .map(announcement -> AnnouncementResult.builder()
                                .id(announcement.getId())
                                .title(announcement.getTitle())
                                .content(announcement.getContent())
                                .scope(announcement.getScope())
                                .displayUntil(announcement.hasDisplayUntil() ? announcement.getDisplayUntil() : null)
                                .createdAt(announcement.getCreatedAt())
                                .build())
                        .toList())
                .meta(AnnouncementListMetaResult.builder()
                        .md5Hash(response.getMeta().getMd5Hash())
                        .sha256Hash(response.getMeta().getSha256Hash())
                        .count(response.getMeta().getCount())
                        .generatedAt(response.getMeta().getGeneratedAt())
                        .build())
                .build();
    }

    /**
     * 获取单个公告详情
     *
     * @param request 公告查询请求
     * @return 公告详情
     */
    public AnnouncementResult getAnnouncement(GetAnnouncementRequest request) {
        if (!isGrpcEnabled()) {
            throw new SsoConfigurationException(
                    SsoErrorCode.GRPC_NOT_ENABLED,
                    "getAnnouncement 需要 gRPC 通信，请启用 gRPC 配置"
            );
        }

        log.debug("[聚合层] 获取公告详情: announcementId={}", request.getAnnouncementId());

        // SDK Request → Protobuf Request
        var grpcRequest = com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetAnnouncementRequest.newBuilder()
                .setAnnouncementId(request.getAnnouncementId())
                .build();

        // 调用 gRPC 客户端
        var response = grpcClient.getAnnouncement(grpcRequest);
        var announcement = response.getAnnouncement();

        // Protobuf Response → SDK Result
        return AnnouncementResult.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .scope(announcement.getScope())
                .displayUntil(announcement.hasDisplayUntil() ? announcement.getDisplayUntil() : null)
                .createdAt(announcement.getCreatedAt())
                .build();
    }

    /**
     * 转换商户标签列表
     */
    private List<MerchantTagResult> toMerchantTags(List<com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.MerchantTag> tags) {
        return tags.stream()
                .map(tag -> MerchantTagResult.builder()
                        .id(tag.getId())
                        .code(tag.getCode())
                        .name(tag.getName())
                        .description(tag.hasDescription() ? tag.getDescription() : null)
                        .color(tag.hasColor() ? tag.getColor() : null)
                        .icon(tag.hasIcon() ? tag.getIcon() : null)
                        .sortOrder(tag.getSortOrder())
                        .status(tag.getStatus())
                        .build())
                .toList();
    }
}
