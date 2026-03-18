package com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.merchant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 最近公告列表结果 DTO
 * <p>
 * 封装最近公告列表及其元信息。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentAnnouncementsResult implements Serializable {

    @Serial
    private static final long serialVersionUID = -1268008424201825214L;

    /**
     * 公告列表
     */
    private List<AnnouncementResult> announcements;

    /**
     * 元信息
     */
    private AnnouncementListMetaResult meta;
}
