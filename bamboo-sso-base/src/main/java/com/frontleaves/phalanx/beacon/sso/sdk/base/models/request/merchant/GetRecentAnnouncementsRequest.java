package com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.merchant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 获取最近公告请求 DTO
 * <p>
 * 封装获取最近公告列表所需的参数。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetRecentAnnouncementsRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -2497483929024998560L;

    /**
     * 限制数量（可选）
     */
    private Integer limit;

    /**
     * 是否仅返回未过期的公告
     */
    private Boolean activeOnly;
}
