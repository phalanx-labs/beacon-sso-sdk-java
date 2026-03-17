package com.frontleaves.phalanx.beacon.sso.sdk.springboot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商户标签信息
 * <p>
 * 商户定义的用户标签数据，包含标签的标识、展示信息和排序等元数据。
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
public class MerchantTag {

    /**
     * 标签 ID（雪花 ID 格式）
     */
    private String id;

    /**
     * 标签代码（用于匹配，仅允许字母数字下划线）
     */
    private String code;

    /**
     * 标签名称（展示用）
     */
    private String name;

    /**
     * 标签描述
     */
    private String description;

    /**
     * 颜色代码（HEX 格式）
     */
    private String color;

    /**
     * 图标标识
     */
    private String icon;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 状态（0 禁用 / 1 启用）
     */
    private Integer status;
}
