package com.frontleaves.phalanx.beacon.sso.sdk.springboot.controller;

import com.frontleaves.phalanx.beacon.sso.sdk.base.api.SsoMerchantApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoApi;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.merchant.CheckUserHasTagRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.merchant.GetAnnouncementRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.merchant.GetMerchantTagsRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.merchant.GetRecentAnnouncementsRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.merchant.GetUserTagsRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.merchant.AnnouncementListMetaResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.merchant.AnnouncementResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.merchant.MerchantTagResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.merchant.RecentAnnouncementsResult;
import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.mvc.ResultUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商户信息控制器
 * <p>
 * 提供商户相关的信息查询 HTTP 端点，包括标签管理、用户标签检查和公告获取。
 * 通过 gRPC 与 SSO 服务通信获取商户数据。
 * </p>
 *
 * <p><b>端点列表：</b></p>
 * <ul>
 *   <li>GET /merchant/tags - 获取商户标签列表</li>
 *   <li>GET /merchant/users/{userId}/tags - 获取指定用户的标签列表</li>
 *   <li>GET /merchant/users/{userId}/tags/{tagCode}/check - 检查用户是否拥有指定标签</li>
 *   <li>GET /merchant/announcements - 获取最近公告列表</li>
 *   <li>GET /merchant/announcements/{announcementId} - 获取单个公告详情</li>
 * </ul>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RestController
@RequestMapping("${beacon.sso.merchant-path:/api/v1/merchant}")
@RequiredArgsConstructor
public class MerchantController {

    private final SsoApi ssoApi;

    /**
     * 获取商户标签列表
     * <p>
     * 获取当前应用所属商户的所有标签，支持按启用状态过滤。
     * </p>
     *
     * @param enabledOnly 是否仅返回启用的标签（可选，默认 false）
     * @return 商户标签列表响应
     */
    @GetMapping("/tags")
    public ResponseEntity<BaseResponse<List<MerchantTagResult>>> getMerchantTags(
            @RequestParam(value = "enabled_only", required = false) Boolean enabledOnly
    ) {
        log.info("处理获取商户标签请求");

        // 构建 SDK Request
        GetMerchantTagsRequest sdkRequest = GetMerchantTagsRequest.builder()
                .enabledOnly(enabledOnly)
                .build();

        try {
            List<MerchantTagResult> tags = ssoApi.merchant().getMerchantTags(sdkRequest);
            return ResultUtil.success("获取商户标签成功", tags.isEmpty() ? null : tags);
        } catch (Exception e) {
            log.warn("Get merchant tags failed: {}", e.getMessage(), e);
            ErrorCode errorCode = this.mapExceptionToErrorCode(e);
            return ResultUtil.error(errorCode, e.getMessage(), null);
        }
    }

    /**
     * 获取用户标签列表
     * <p>
     * 获取指定用户在当前商户下的所有标签。
     * </p>
     *
     * @param userId 用户 ID
     * @return 用户标签列表响应
     */
    @GetMapping("/users/{userId}/tags")
    public ResponseEntity<BaseResponse<List<MerchantTagResult>>> getUserTags(
            @PathVariable String userId
    ) {
        log.info("Processing get user tags request: {}", userId);

        if (userId == null || userId.isBlank()) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少用户 ID", null);
        }

        // 构建 SDK Request
        GetUserTagsRequest sdkRequest = GetUserTagsRequest.builder()
                .userId(userId)
                .build();

        try {
            List<MerchantTagResult> tags = ssoApi.merchant().getUserTags(sdkRequest);
            return ResultUtil.success("获取用户标签成功", tags.isEmpty() ? null : tags);
        } catch (Exception e) {
            log.warn("Get user tags failed: {}", e.getMessage(), e);
            ErrorCode errorCode = this.mapExceptionToErrorCode(e);
            return ResultUtil.error(errorCode, e.getMessage(), null);
        }
    }

    /**
     * 检查用户是否拥有指定标签
     * <p>
     * 检查指定用户是否拥有指定的商户标签。
     * </p>
     *
     * @param userId  用户 ID
     * @param tagCode 标签代码
     * @return 检查结果响应
     */
    @GetMapping("/users/{userId}/tags/{tagCode}/check")
    public ResponseEntity<BaseResponse<Boolean>> checkUserHasTag(
            @PathVariable String userId,
            @PathVariable String tagCode
    ) {
        log.info("Processing check user has tag request: userId={}, tagCode={}", userId, tagCode);

        if (userId == null || userId.isBlank()) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少用户 ID", null);
        }
        if (tagCode == null || tagCode.isBlank()) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少标签代码", null);
        }

        // 构建 SDK Request
        CheckUserHasTagRequest sdkRequest = CheckUserHasTagRequest.builder()
                .userId(userId)
                .tagCode(tagCode)
                .build();

        try {
            boolean hasTag = ssoApi.merchant().checkUserHasTag(sdkRequest);
            return ResultUtil.success("检查用户标签成功", hasTag);
        } catch (Exception e) {
            log.warn("Check user has tag failed: {}", e.getMessage(), e);
            ErrorCode errorCode = this.mapExceptionToErrorCode(e);
            return ResultUtil.error(errorCode, e.getMessage(), null);
        }
    }

    /**
     * 获取最近公告列表
     * <p>
     * 获取当前商户的最近公告列表，支持限制返回数量和过滤过期公告。
     * </p>
     *
     * @param limit      最大返回数量（可选，默认 10，最大 20）
     * @param activeOnly 是否仅返回未过期的公告（可选，默认 false）
     * @return 最近公告列表响应
     */
    @GetMapping("/announcements")
    public ResponseEntity<BaseResponse<RecentAnnouncementsResult>> getRecentAnnouncements(
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "active_only", required = false) Boolean activeOnly
    ) {
        log.info("Processing get recent announcements request");

        // 构建 SDK Request
        GetRecentAnnouncementsRequest sdkRequest = GetRecentAnnouncementsRequest.builder()
                .limit(limit)
                .activeOnly(activeOnly)
                .build();

        try {
            RecentAnnouncementsResult data = ssoApi.merchant().getRecentAnnouncements(sdkRequest);
            return ResultUtil.success("获取最近公告成功", data);
        } catch (Exception e) {
            log.warn("Get recent announcements failed: {}", e.getMessage(), e);
            ErrorCode errorCode = this.mapExceptionToErrorCode(e);
            return ResultUtil.error(errorCode, e.getMessage(), null);
        }
    }

    /**
     * 获取单个公告详情
     * <p>
     * 根据公告 ID 获取公告的完整详情。
     * </p>
     *
     * @param announcementId 公告 ID
     * @return 公告详情响应
     */
    @GetMapping("/announcements/{announcementId}")
    public ResponseEntity<BaseResponse<AnnouncementResult>> getAnnouncement(
            @PathVariable String announcementId
    ) {
        log.info("Processing get announcement request: {}", announcementId);

        if (announcementId == null || announcementId.isBlank()) {
            return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "缺少公告 ID", null);
        }

        // 构建 SDK Request
        GetAnnouncementRequest sdkRequest = GetAnnouncementRequest.builder()
                .announcementId(announcementId)
                .build();

        try {
            AnnouncementResult data = ssoApi.merchant().getAnnouncement(sdkRequest);
            return ResultUtil.success("获取公告详情成功", data);
        } catch (Exception e) {
            log.warn("Get announcement failed: {}", e.getMessage(), e);
            ErrorCode errorCode = this.mapExceptionToErrorCode(e);
            return ResultUtil.error(errorCode, e.getMessage(), null);
        }
    }

    private ErrorCode mapExceptionToErrorCode(Exception e) {
        String message = e.getMessage();
        if (message == null) {
            return ErrorCode.OPERATION_FAILED;
        }
        if (message.contains("Missing") || message.contains("missing")
                || message.contains("not found") || message.contains("Not found")) {
            return ErrorCode.PARAMETER_MISSING;
        }
        if (message.contains("configuration") || message.contains("Configuration")) {
            return ErrorCode.CONFIGURATION_ERROR;
        }
        return ErrorCode.OPERATION_FAILED;
    }
}
