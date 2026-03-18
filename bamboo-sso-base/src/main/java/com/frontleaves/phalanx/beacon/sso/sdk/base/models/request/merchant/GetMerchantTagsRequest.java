package com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.merchant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 获取商户标签请求 DTO
 * <p>
 * 封装获取商户标签所需的参数。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMerchantTagsRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 商户 ID
     */
    private String merchantId;
}
