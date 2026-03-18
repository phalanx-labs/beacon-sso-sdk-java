package com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.merchant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 公告列表元信息结果 DTO
 * <p>
 * 封装公告列表的元信息。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementListMetaResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * MD5 哈希值
     */
    private String md5Hash;

    /**
     * SHA256 哈希值
     */
    private String sha256Hash;

    /**
     * 公告数量
     */
    private Integer count;

    /**
     * 生成时间
     */
    private String generatedAt;
}
