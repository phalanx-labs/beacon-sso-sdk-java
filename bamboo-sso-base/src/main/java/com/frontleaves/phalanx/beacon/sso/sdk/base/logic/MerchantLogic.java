package com.frontleaves.phalanx.beacon.sso.sdk.base.logic;

import com.frontleaves.phalanx.beacon.sso.sdk.base.grpc.SsoGrpcMerchantClient;
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
import lombok.extern.slf4j.Slf4j;

/**
 * 商户业务逻辑组件
 * <p>
 * 通过 gRPC 商户服务处理标签管理和公告查询等商户相关业务。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
public class MerchantLogic {

    private final SsoGrpcMerchantClient ssoGrpcMerchantClient;

    /**
     * 构造商户业务逻辑组件
     *
     * @param grpcMerchantClient gRPC 商户服务客户端
     */
    public MerchantLogic(SsoGrpcMerchantClient grpcMerchantClient) {
        ssoGrpcMerchantClient = grpcMerchantClient;
    }

    /**
     * 获取当前应用所属商户的所有标签
     *
     * @param request 获取商户标签请求
     * @return 商户标签列表响应
     */
    public GetMerchantTagsResponse getMerchantTags(GetMerchantTagsRequest request) {
        log.info("GetMerchantTags - 获取商户标签列表");
        return ssoGrpcMerchantClient.getMerchantTags(request);
    }

    /**
     * 获取指定用户在当前商户的所有标签
     *
     * @param request 获取用户标签请求
     * @return 用户标签列表响应
     */
    public GetUserTagsResponse getUserTags(GetUserTagsRequest request) {
        log.info("GetUserTags - 获取用户标签列表");
        return ssoGrpcMerchantClient.getUserTags(request);
    }

    /**
     * 检查用户是否有指定标签
     *
     * @param request 检查用户标签请求
     * @return 检查结果响应
     */
    public CheckUserHasTagResponse checkUserHasTag(CheckUserHasTagRequest request) {
        log.info("CheckUserHasTag - 检查用户标签");
        return ssoGrpcMerchantClient.checkUserHasTag(request);
    }

    /**
     * 获取最近公告列表
     *
     * @param request 获取最近公告请求
     * @return 最近公告列表响应
     */
    public GetRecentAnnouncementsResponse getRecentAnnouncements(GetRecentAnnouncementsRequest request) {
        log.info("GetRecentAnnouncements - 获取最近公告列表");
        return ssoGrpcMerchantClient.getRecentAnnouncements(request);
    }

    /**
     * 获取单个公告详情
     *
     * @param request 获取公告详情请求
     * @return 公告详情响应
     */
    public GetAnnouncementResponse getAnnouncement(GetAnnouncementRequest request) {
        log.info("GetAnnouncement - 获取公告详情");
        return ssoGrpcMerchantClient.getAnnouncement(request);
    }
}
