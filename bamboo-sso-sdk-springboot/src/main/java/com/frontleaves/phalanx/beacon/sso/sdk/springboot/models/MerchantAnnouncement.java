package com.frontleaves.phalanx.beacon.sso.sdk.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商户公告信息
 * <p>
 * 商户发布的公告数据，包含标题、内容和展示范围等信息。
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
public class MerchantAnnouncement {

    /**
     * 公告 ID（雪花 ID 格式）
     */
    private String id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 展示范围
     */
    private Integer scope;

    /**
     * 展示截止时间（ISO 8601 格式）
     */
    private String displayUntil;

    /**
     * 创建时间（ISO 8601 格式）
     */
    private String createdAt;
}
