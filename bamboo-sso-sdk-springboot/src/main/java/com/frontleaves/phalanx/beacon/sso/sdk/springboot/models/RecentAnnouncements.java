package com.frontleaves.phalanx.beacon.sso.sdk.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 获取最近公告响应
 * <p>
 * 最近公告列表的响应数据，包含公告列表和元信息。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecentAnnouncements {

    /**
     * 公告列表
     */
    private List<MerchantAnnouncement> announcements;

    /**
     * 列表元信息
     */
    private AnnouncementListMeta meta;
}
