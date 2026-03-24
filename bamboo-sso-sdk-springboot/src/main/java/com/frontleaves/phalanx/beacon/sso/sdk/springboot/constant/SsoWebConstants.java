/*
 * Copyright (c) 2025 FrontLeaves. All rights reserved.
 *
 * This software is the confidential and proprietary information of FrontLeaves.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with FrontLeaves.
 */
package com.frontleaves.phalanx.beacon.sso.sdk.springboot.constant;

/**
 * Spring Boot Web 层常量定义
 * <p>
 * 包含请求属性键、Content-Type、OAuth 令牌类型提示等 Web 层特有常量。
 * </p>
 *
 * @since 1.0.0
 */
public final class SsoWebConstants {

    private SsoWebConstants() {
        throw new UnsupportedOperationException("Constant class cannot be instantiated");
    }

    // ==================== 请求属性键 ====================

    /**
     * 请求属性键：令牌自省信息
     */
    public static final String ATTR_INTROSPECTION = "beacon.sso.introspection";

    /**
     * 请求属性键：访问令牌
     */
    public static final String ATTR_ACCESS_TOKEN = "beacon.sso.access_token";

    // ==================== Content-Type ====================

    /**
     * JSON 内容类型（UTF-8）
     */
    public static final String CONTENT_TYPE_JSON_UTF8 = "application/json;charset=UTF-8";

    // ==================== OAuth 令牌类型提示（RFC 标准，小写） ====================

    /**
     * 访问令牌类型提示
     * <p>
     * 用于 OAuth 2.0 Token Revocation (RFC 7009) 的 token_type_hint 参数。
     * </p>
     */
    public static final String TOKEN_TYPE_HINT_ACCESS = "access_token";

    /**
     * 刷新令牌类型提示
     * <p>
     * 用于 OAuth 2.0 Token Revocation (RFC 7009) 的 token_type_hint 参数。
     * </p>
     */
    public static final String TOKEN_TYPE_HINT_REFRESH = "refresh_token";

    // ==================== 默认路径 ====================

    /**
     * 默认 API 路径前缀
     */
    public static final String DEFAULT_PATH_PREFIX = "/api/v1/beacon";
}
