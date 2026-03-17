package com.frontleaves.phalanx.beacon.sso.sdk.base.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 最近公告响应 DTO
 * <p>
 * 对应 gRPC GetRecentAnnouncementsResponse，封装最近公告列表及其元信息。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoRecentAnnouncementsResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 公告列表
     */
    private List<SsoMerchantAnnouncement> announcements;

    /**
     * 元信息
     */
    private SsoAnnouncementListMeta meta;
}
