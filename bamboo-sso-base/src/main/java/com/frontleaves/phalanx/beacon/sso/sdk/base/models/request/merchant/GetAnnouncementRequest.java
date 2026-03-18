package com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.merchant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 获取公告详情请求 DTO
 * <p>
 * 封装获取单个公告详情所需的参数。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAnnouncementRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 公告 ID
     */
    private String announcementId;
}
