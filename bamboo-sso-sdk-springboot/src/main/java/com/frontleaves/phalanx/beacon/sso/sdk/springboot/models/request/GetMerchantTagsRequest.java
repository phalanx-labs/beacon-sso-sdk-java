package com.frontleaves.phalanx.beacon.sso.sdk.springboot.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取商户标签列表请求
 * <p>
 * 用于查询商户标签列表的请求参数，支持按启用状态过滤。
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
public class GetMerchantTagsRequest {

    /**
     * 是否仅返回启用的标签
     */
    private Boolean enabledOnly;
}
