package com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.merchant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 商户标签结果 DTO
 * <p>
 * 封装商户标签信息。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantTagResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 标签 ID
     */
    private String id;

    /**
     * 标签代码
     */
    private String code;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 颜色代码（HEX）
     */
    private String color;

    /**
     * 图标标识
     */
    private String icon;

    /**
     * 排序权重
     */
    private Integer sortOrder;

    /**
     * 状态（0禁用/1启用）
     */
    private Integer status;
}
