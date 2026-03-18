package com.frontleaves.phalanx.beacon.sso.sdk.base.utility;

import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.account.LoginResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.account.RegisterResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.common.RoleResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.merchant.AnnouncementListMetaResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.merchant.AnnouncementResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.merchant.MerchantTagResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.merchant.RecentAnnouncementsResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.user.UserDetailResult;
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
    public RegisterResult toRegisterResult(RegisterByEmailResponse response) {
        return RegisterResult.builder()
                .userId(response.getUserId())
                .token(response.getToken())
                .build();
    }

    /**
     * 转换登录响应
     */
    public LoginResult toLoginResult(PasswordLoginResponse response) {
        return LoginResult.builder()
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
    public UserDetailResult toUserDetail(User user) {
        return UserDetailResult.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.hasEmail() ? user.getEmail() : null)
                .emailVerified(user.getIsEmailVerified())
                .avatar(user.hasAvatar() ? user.getAvatar() : null)
                .phone(user.hasPhone() ? user.getPhone() : null)
                .phoneVerified(user.getIsPhoneVerified())
                .gender(user.getGender())
                .birthday(user.hasBirthday() ? user.getBirthday() : null)
                .status(user.getStatus())
                .needResetPassword(user.getNeedResetPassword())
                .lastLoginAt(user.hasLastLoginAt() ? user.getLastLoginAt() : null)
                .lastLoginIp(user.hasLastLoginIp() ? user.getLastLoginIp() : null)
                .roles(toRoles(user.getRolesList()))
                .build();
    }

    /**
     * 转换角色列表
     */
    public List<RoleResult> toRoles(List<Role> roles) {
        return roles.stream()
                .map(this::toRole)
                .toList();
    }

    /**
     * 转换单个角色
     */
    public RoleResult toRole(Role role) {
        return RoleResult.builder()
                .code(role.getCode())
                .name(role.getName())
                .description(role.hasDescription() ? role.getDescription() : null)
                .build();
    }

    /**
     * 转换商户标签
     */
    public MerchantTagResult toMerchantTag(MerchantTag tag) {
        return MerchantTagResult.builder()
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
    public List<MerchantTagResult> toMerchantTags(List<MerchantTag> tags) {
        return tags.stream()
                .map(this::toMerchantTag)
                .toList();
    }

    /**
     * 转换商户公告
     */
    public AnnouncementResult toAnnouncement(MerchantAnnouncement announcement) {
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
     * 转换商户公告列表
     */
    public List<AnnouncementResult> toAnnouncements(List<MerchantAnnouncement> announcements) {
        return announcements.stream()
                .map(this::toAnnouncement)
                .toList();
    }

    /**
     * 转换公告列表元信息
     */
    public AnnouncementListMetaResult toAnnouncementListMeta(AnnouncementListMeta meta) {
        return AnnouncementListMetaResult.builder()
                .md5Hash(meta.getMd5Hash())
                .sha256Hash(meta.getSha256Hash())
                .count(meta.getCount())
                .generatedAt(meta.getGeneratedAt())
                .build();
    }

    /**
     * 转换最近公告响应
     */
    public RecentAnnouncementsResult toRecentAnnouncementsResult(GetRecentAnnouncementsResponse response) {
        return RecentAnnouncementsResult.builder()
                .announcements(toAnnouncements(response.getAnnouncementsList()))
                .meta(toAnnouncementListMeta(response.getMeta()))
                .build();
    }
}
