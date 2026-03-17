package com.frontleaves.phalanx.beacon.sso.sdk.base.utility;

import com.frontleaves.phalanx.beacon.sso.sdk.base.models.*;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.*;

import java.util.List;

/**
 * Protobuf → DTO 转换工具
 * <p>
 * 在 gRPC 传输层与 API 层之间进行类型转换，
 * 确保 protobuf 类型仅存在于内部传输层。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
public class GrpcModelConverter {

    /**
     * 转换注册响应
     */
    public SsoRegisterResult toRegisterResult(RegisterByEmailResponse response) {
        return SsoRegisterResult.builder()
                .userId(response.getUserId())
                .token(response.getToken())
                .build();
    }

    /**
     * 转换登录响应
     */
    public SsoLoginResult toLoginResult(PasswordLoginResponse response) {
        return SsoLoginResult.builder()
                .accessToken(response.getAccessToken())
                .tokenType(response.getTokenType())
                .expiresIn(response.getExpiresIn())
                .refreshToken(response.hasRefreshToken() ? response.getRefreshToken() : null)
                .scope(response.hasScope() ? response.getScope() : null)
                .idToken(response.hasIdToken() ? response.getIdToken() : null)
                .build();
    }

    /**
     * 转换用户详细信息
     */
    public SsoUserDetail toUserDetail(User user) {
        return SsoUserDetail.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.hasEmail() ? user.getEmail() : null)
                .phone(user.hasPhone() ? user.getPhone() : null)
                .avatar(user.hasAvatar() ? user.getAvatar() : null)
                .gender(user.getGender())
                .birthday(user.hasBirthday() ? user.getBirthday() : null)
                .status(user.getStatus())
                .emailVerified(user.getIsEmailVerified())
                .phoneVerified(user.getIsPhoneVerified())
                .needResetPassword(user.getNeedResetPassword())
                .lastLoginAt(user.hasLastLoginAt() ? user.getLastLoginAt() : null)
                .lastLoginIp(user.hasLastLoginIp() ? user.getLastLoginIp() : null)
                .roles(toRoles(user.getRolesList()))
                .build();
    }

    /**
     * 转换角色列表
     */
    public List<SsoRole> toRoles(List<Role> roles) {
        return roles.stream()
                .map(this::toRole)
                .toList();
    }

    /**
     * 转换单个角色
     */
    public SsoRole toRole(Role role) {
        return SsoRole.builder()
                .code(role.getCode())
                .name(role.getName())
                .description(role.hasDescription() ? role.getDescription() : null)
                .build();
    }

    /**
     * 转换商户标签
     */
    public SsoMerchantTag toMerchantTag(MerchantTag tag) {
        return SsoMerchantTag.builder()
                .id(tag.getId())
                .code(tag.getCode())
                .name(tag.getName())
                .description(tag.hasDescription() ? tag.getDescription() : null)
                .color(tag.hasColor() ? tag.getColor() : null)
                .icon(tag.hasIcon() ? tag.getIcon() : null)
                .sortOrder(tag.getSortOrder())
                .status(tag.getStatus())
                .build();
    }

    /**
     * 转换商户标签列表
     */
    public List<SsoMerchantTag> toMerchantTags(List<MerchantTag> tags) {
        return tags.stream()
                .map(this::toMerchantTag)
                .toList();
    }

    /**
     * 转换商户公告
     */
    public SsoMerchantAnnouncement toAnnouncement(MerchantAnnouncement announcement) {
        return SsoMerchantAnnouncement.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .scope(announcement.getScope())
                .displayUntil(announcement.hasDisplayUntil() ? announcement.getDisplayUntil() : null)
                .createdAt(announcement.getCreatedAt())
                .build();
    }

    /**
     * 转换商户公告列表
     */
    public List<SsoMerchantAnnouncement> toAnnouncements(List<MerchantAnnouncement> announcements) {
        return announcements.stream()
                .map(this::toAnnouncement)
                .toList();
    }

    /**
     * 转换公告列表元信息
     */
    public SsoAnnouncementListMeta toAnnouncementListMeta(AnnouncementListMeta meta) {
        return SsoAnnouncementListMeta.builder()
                .md5Hash(meta.getMd5Hash())
                .sha256Hash(meta.getSha256Hash())
                .count(meta.getCount())
                .generatedAt(meta.getGeneratedAt())
                .build();
    }

    /**
     * 转换最近公告响应
     */
    public SsoRecentAnnouncementsResult toRecentAnnouncementsResult(GetRecentAnnouncementsResponse response) {
        return SsoRecentAnnouncementsResult.builder()
                .announcements(toAnnouncements(response.getAnnouncementsList()))
                .meta(toAnnouncementListMeta(response.getMeta()))
                .build();
    }
}
