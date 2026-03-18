package com.frontleaves.phalanx.beacon.sso.sdk.base.models.request.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 密码登录请求 DTO
 * <p>
 * 封装密码登录所需的参数，包括用户名、密码和可选的客户端信息。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordLoginRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -899925074585425347L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 授权范围
     */
    private String scope;

    /**
     * 客户端 IP
     */
    private String clientIp;

    /**
     * User-Agent
     */
    private String userAgent;
}
