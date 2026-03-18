package com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.merchant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 公告结果 DTO
 * <p>
 * 封装单个公告信息。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 公告 ID
     */
    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 作用域（1: 商户，2: 应用）
     */
    private Integer scope;

    /**
     * 显示截止时间
     */
    private String displayUntil;

    /**
     * 创建时间
     */
    private String createdAt;
}
