package com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.merchant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 检查用户标签请求 DTO
 * <p>
 * 封装检查用户是否拥有指定标签所需的参数。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckUserHasTagRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -4890930649558557562L;

    /**
     * 用户 ID
     */
    private String userId;

    /**
     * 标签代码
     */
    private String tagCode;
}
