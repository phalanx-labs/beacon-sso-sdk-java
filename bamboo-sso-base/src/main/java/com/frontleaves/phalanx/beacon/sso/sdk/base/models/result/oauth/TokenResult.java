package com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 令牌结果 DTO
 * <p>
 * 封装令牌操作后返回的结果信息。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResult implements Serializable {

    @Serial
    private static final long serialVersionUID = -307388624552916449L;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 令牌类型
     */
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * 过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 作用域
     */
    private String scope;

    /**
     * 创建时间戳
     */
    private Long createdAt;
}
