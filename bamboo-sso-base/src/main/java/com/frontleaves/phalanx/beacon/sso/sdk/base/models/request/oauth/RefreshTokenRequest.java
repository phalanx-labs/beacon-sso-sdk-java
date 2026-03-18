package com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 刷新令牌请求 DTO
 * <p>
 * 封装刷新令牌所需的参数。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -8685668429193943568L;

    /**
     * 刷新令牌
     */
    private String refreshToken;
}
