package com.frontleaves.phalanx.beacon.sso.sdk.base.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 商户公告 DTO
 * <p>
 * 对应 gRPC MerchantAnnouncement，封装商户公告信息。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoMerchantAnnouncement implements Serializable {

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
     * 范围（1商户/2应用）
     */
    private int scope;

    /**
     * 展示截止时间
     */
    private String displayUntil;

    /**
     * 创建时间
     */
    private String createdAt;
}
