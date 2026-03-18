package com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户信息结果 DTO（最小公共字段）
 * <p>
 * 遵循最小性质原则，仅包含 HTTP 和 gRPC 共有的最小字段。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserinfoResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一标识
     */
    private String sub;

    /**
     * 显示名称
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 邮箱是否验证
     */
    private Boolean emailVerified;

    /**
     * 头像 URL
     */
    private String picture;
}
