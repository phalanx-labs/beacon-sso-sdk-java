package com.frontleaves.phalanx.beacon.sso.sdk.base.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 公告列表元信息 DTO
 * <p>
 * 对应 gRPC AnnouncementListMeta，封装公告列表的哈希校验和统计信息。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoAnnouncementListMeta implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * MD5 哈希
     */
    private String md5Hash;

    /**
     * SHA256 哈希
     */
    private String sha256Hash;

    /**
     * 公告数量
     */
    private int count;

    /**
     * 生成时间
     */
    private String generatedAt;
}
