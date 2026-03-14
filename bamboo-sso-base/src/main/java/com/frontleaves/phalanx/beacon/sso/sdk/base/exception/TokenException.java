package com.frontleaves.phalanx.beacon.sso.sdk.base.exception;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;

import java.io.Serial;
import java.util.Optional;

/**
 * Token 相关异常
 * <p>
 * 当 Token 处理过程中发生错误时抛出此异常。
 * 记录导致错误的 Token 类型以便于调试和日志追踪。
 * </p>
 *
 * @author Xiao Lfeng &lt;xiao_lfeng@icloud.com&gt;
 * @since 1.0.0
 */
public class TokenException extends SsoException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Token 类型常量
     */
    public static final String TOKEN_TYPE_ACCESS = "ACCESS_TOKEN";
    public static final String TOKEN_TYPE_REFRESH = "REFRESH_TOKEN";
    public static final String TOKEN_TYPE_ID = "ID_TOKEN";
    public static final String TOKEN_TYPE_AUTHORIZATION_CODE = "AUTHORIZATION_CODE";

    /**
     * Token 类型（可选）
     */
    private final String tokenType;

    /**
     * 使用默认错误码构造异常
     */
    public TokenException() {
        super(SsoErrorCode.TOKEN_INVALID);
        this.tokenType = null;
    }

    /**
     * 使用错误码构造异常
     *
     * @param errorCode 错误码枚举
     */
    public TokenException(SsoErrorCode errorCode) {
        super(errorCode);
        this.tokenType = null;
    }

    /**
     * 使用自定义消息构造异常
     *
     * @param message 自定义错误消息
     */
    public TokenException(String message) {
        super(SsoErrorCode.TOKEN_INVALID, message);
        this.tokenType = null;
    }

    /**
     * 使用 Token 类型构造异常
     *
     * @param tokenType Token 类型
     * @param message   自定义错误消息
     */
    public TokenException(String tokenType, String message) {
        super(SsoErrorCode.TOKEN_INVALID, message);
        this.tokenType = tokenType;
    }

    /**
     * 使用错误码和 Token 类型构造异常
     *
     * @param errorCode 错误码枚举
     * @param tokenType Token 类型
     */
    public TokenException(SsoErrorCode errorCode, String tokenType) {
        super(errorCode);
        this.tokenType = tokenType;
    }

    /**
     * 使用错误码、消息和 Token 类型构造异常
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误消息
     * @param tokenType Token 类型
     */
    public TokenException(SsoErrorCode errorCode, String message, String tokenType) {
        super(errorCode, message);
        this.tokenType = tokenType;
    }

    /**
     * 使用原因构造异常
     *
     * @param cause 异常原因
     */
    public TokenException(Throwable cause) {
        super(SsoErrorCode.TOKEN_INVALID, cause);
        this.tokenType = null;
    }

    /**
     * 使用消息和原因构造异常
     *
     * @param message 自定义错误消息
     * @param cause   异常原因
     */
    public TokenException(String message, Throwable cause) {
        super(SsoErrorCode.TOKEN_INVALID, message, cause);
        this.tokenType = null;
    }

    /**
     * 使用完整参数构造异常
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误消息
     * @param cause     异常原因
     * @param tokenType Token 类型
     */
    public TokenException(SsoErrorCode errorCode, String message, Throwable cause, String tokenType) {
        super(errorCode, message, cause);
        this.tokenType = tokenType;
    }

    /**
     * 获取 Token 类型
     *
     * @return Token 类型的 {@link Optional}，如果未设置则返回空
     */
    public Optional<String> getTokenType() {
        return Optional.ofNullable(tokenType);
    }

    /**
     * 判断是否为 Access Token 相关异常
     *
     * @return 如果是 Access Token 异常返回 {@code true}
     */
    public boolean isAccessTokenException() {
        return TOKEN_TYPE_ACCESS.equals(tokenType);
    }

    /**
     * 判断是否为 Refresh Token 相关异常
     *
     * @return 如果是 Refresh Token 异常返回 {@code true}
     */
    public boolean isRefreshTokenException() {
        return TOKEN_TYPE_REFRESH.equals(tokenType);
    }

    /**
     * 判断是否为 ID Token 相关异常
     *
     * @return 如果是 ID Token 异常返回 {@code true}
     */
    public boolean isIdTokenException() {
        return TOKEN_TYPE_ID.equals(tokenType);
    }

    /**
     * 创建 Access Token 异常
     *
     * @param errorCode 错误码
     * @param message   错误消息
     * @return TokenException 实例
     */
    public static TokenException accessTokenError(SsoErrorCode errorCode, String message) {
        return new TokenException(errorCode, message, TOKEN_TYPE_ACCESS);
    }

    /**
     * 创建 Refresh Token 异常
     *
     * @param errorCode 错误码
     * @param message   错误消息
     * @return TokenException 实例
     */
    public static TokenException refreshTokenError(SsoErrorCode errorCode, String message) {
        return new TokenException(errorCode, message, TOKEN_TYPE_REFRESH);
    }

    /**
     * 创建 ID Token 异常
     *
     * @param errorCode 错误码
     * @param message   错误消息
     * @return TokenException 实例
     */
    public static TokenException idTokenError(SsoErrorCode errorCode, String message) {
        return new TokenException(errorCode, message, TOKEN_TYPE_ID);
    }

    /**
     * 获取格式化的错误消息
     *
     * @return 包含 Token 类型信息（如果有）的格式化错误消息
     */
    @Override
    public String getFormattedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(getErrorCodeString()).append("] ").append(getMessage());

        getTokenType().ifPresent(type -> sb.append(" | TokenType: ").append(type));

        return sb.toString();
    }

    /**
     * 转换为字符串
     *
     * @return 格式化的异常信息
     */
    @Override
    public String toString() {
        return getClass().getName() + ": " + getFormattedMessage();
    }
}
