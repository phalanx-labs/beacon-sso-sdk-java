package com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 授权 URL 结果 DTO
 * <p>
 * 封装生成授权 URL 后返回的结果信息。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationUrlResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 授权 URL
     */
    private String url;

    /**
     * 状态参数
     */
    private String state;

    /**
     * PKCE code_challenge
     */
    private String codeChallenge;

    /**
     * PKCE code_verifier（用于后续交换）
     */
    private String codeVerifier;
}
