package com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 令牌验证结果 DTO
 * <p>
 * 封装令牌验证后返回的结果信息。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 令牌是否有效
     */
    private Boolean valid;

    /**
     * 验证消息
     */
    private String message;
}
