package com.frontleaves.phalanx.beacon.sso.sdk.base.api;

import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.SsoMerchantAnnouncement;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.SsoMerchantTag;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.SsoRecentAnnouncementsResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.GrpcModelConverter;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.CheckUserHasTagRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetAnnouncementRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetMerchantTagsRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetRecentAnnouncementsRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetUserTagsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 商户操作（gRPC-only）
 * <p>
 * 封装商户相关操作，包括标签管理和公告获取。
 * 所有方法均通过 gRPC 协议与 SSO 服务通信。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RequiredArgsConstructor
public class SsoMerchantApi {

    private final SsoRequest ssoRequest;
    private final GrpcModelConverter converter;

    /**
     * 获取商户标签列表
     *
     * @param request 标签查询请求
     * @return 商户标签列表
     */
    public List<SsoMerchantTag> getMerchantTags(GetMerchantTagsRequest request) {
        log.debug("获取商户标签列表");
        return converter.toMerchantTags(
                ssoRequest.merchant().getMerchantTags(request).getTagsList()
        );
    }

    /**
     * 获取用户标签列表
     *
     * @param request 用户标签查询请求
     * @return 用户标签列表
     */
    public List<SsoMerchantTag> getUserTags(GetUserTagsRequest request) {
        log.debug("获取用户标签列表: userId={}", request.getUserId());
        return converter.toMerchantTags(
                ssoRequest.merchant().getUserTags(request).getTagsList()
        );
    }

    /**
     * 检查用户是否拥有指定标签
     *
     * @param request 标签检查请求
     * @return 是否拥有该标签
     */
    public boolean checkUserHasTag(CheckUserHasTagRequest request) {
        log.debug("检查用户标签: userId={}, tagCode={}", request.getUserId(), request.getTagCode());
        return ssoRequest.merchant().checkUserHasTag(request).getHasTag();
    }

    /**
     * 获取最近公告列表
     *
     * @param request 公告查询请求
     * @return 最近公告响应
     */
    public SsoRecentAnnouncementsResult getRecentAnnouncements(GetRecentAnnouncementsRequest request) {
        log.debug("获取最近公告列表");
        return converter.toRecentAnnouncementsResult(
                ssoRequest.merchant().getRecentAnnouncements(request)
        );
    }

    /**
     * 获取单个公告详情
     *
     * @param request 公告查询请求
     * @return 公告详情
     */
    public SsoMerchantAnnouncement getAnnouncement(GetAnnouncementRequest request) {
        log.debug("获取公告详情: announcementId={}", request.getAnnouncementId());
        return converter.toAnnouncement(
                ssoRequest.merchant().getAnnouncement(request).getAnnouncement()
        );
    }
}
