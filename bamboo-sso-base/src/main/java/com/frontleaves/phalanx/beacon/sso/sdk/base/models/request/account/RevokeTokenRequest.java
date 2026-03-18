package com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 撤销令牌请求 DTO
 * <p>
 * 封装撤销令牌所需的参数，包括令牌和令牌类型。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevokeTokenRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -1226008798638719932L;

    /**
     * 要撤销的令牌
     */
    private String token;

    /**
     * 令牌类型（如 access_token、refresh_token）
     */
    private String tokenType;
}
