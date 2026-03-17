package com.frontleaves.phalanx.beacon.sso.sdk.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公告列表元信息
 * <p>
 * 公告列表的摘要元数据，用于客户端判断公告列表是否需要重新展示。
 * 包含哈希值、计数和生成时间等信息。
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
public class AnnouncementListMeta {

    /**
     * 所有公告内容的 MD5 哈希值（用于快速判断是否有变化）
     */
    private String md5Hash;

    /**
     * 所有公告内容的 SHA256 哈希值（用于精确判断）
     */
    private String sha256Hash;

    /**
     * 公告总数
     */
    private Integer count;

    /**
     * 元信息生成时间（ISO 8601 格式）
     */
    private String generatedAt;
}
