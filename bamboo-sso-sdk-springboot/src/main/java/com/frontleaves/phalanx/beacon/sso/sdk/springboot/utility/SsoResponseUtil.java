package com.frontleaves.phalanx.beacon.sso.sdk.springboot.utility;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SSO 响应工具类
 * <p>
 * 提供统一的 HTTP 响应构建方法，用于快速构建成功、错误、重定向等响应。
 * 所有方法均为静态方法，通过 final class 和 private constructor 防止实例化。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1-SNAPSHOT
 */
public final class SsoResponseUtil {

    /**
     * 私有构造函数，防止实例化
     */
    private SsoResponseUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 返回成功响应
     * <p>
     * 构建包含数据的成功响应，HTTP 状态码为 200 OK。
     * </p>
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 包含数据的成功响应
     */
    public static <T> ResponseEntity<?> success(T data) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("data", data);
        return ResponseEntity.ok(body);
    }

    /**
     * 返回带消息的成功响应
     * <p>
     * 构建包含消息和数据的成功响应，HTTP 状态码为 200 OK。
     * </p>
     *
     * @param message 成功消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return 包含消息和数据的成功响应
     */
    public static <T> ResponseEntity<?> success(String message, T data) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", message);
        body.put("data", data);
        return ResponseEntity.ok(body);
    }

    /**
     * 返回错误响应
     * <p>
     * 构建包含错误消息的响应，HTTP 状态码默认为 400 Bad Request。
     * </p>
     *
     * @param message 错误消息
     * @return 包含错误消息的响应
     */
    public static ResponseEntity<?> error(String message) {
        return error(HttpStatus.BAD_REQUEST.value(), message);
    }

    /**
     * 返回自定义状态码的错误响应
     * <p>
     * 构建包含自定义状态码和错误消息的响应。
     * </p>
     *
     * @param status  HTTP 状态码
     * @param message 错误消息
     * @return 包含状态码和错误消息的响应
     */
    public static ResponseEntity<?> error(int status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("error", message);
        return ResponseEntity.status(status).body(body);
    }

    /**
     * 返回 401 未授权响应
     * <p>
     * 构建未授权响应，通常用于令牌无效或缺失的情况。
     * </p>
     *
     * @param message 错误消息
     * @return 401 未授权响应
     */
    public static ResponseEntity<?> unauthorized(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("error", "unauthorized");
        body.put("message", message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * 返回 403 禁止访问响应
     * <p>
     * 构建禁止访问响应，通常用于权限不足的情况。
     * </p>
     *
     * @param message 错误消息
     * @return 403 禁止访问响应
     */
    public static ResponseEntity<?> forbidden(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("error", "forbidden");
        body.put("message", message);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * 返回重定向响应
     * <p>
     * 构建重定向到指定 URL 的响应，HTTP 状态码为 302 Found。
     * </p>
     *
     * @param url 重定向目标 URL
     * @return 重定向响应
     */
    public static ResponseEntity<Void> redirect(String url) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }
}
