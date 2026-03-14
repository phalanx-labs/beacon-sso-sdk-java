package com.frontleaves.phalanx.beacon.sso.sdk.base.exception;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;

import java.io.Serial;
import java.util.Optional;

/**
 * SSO 基础异常类
 * <p>
 * 所有 SSO SDK 相关异常的基类，继承自 {@link RuntimeException}。
 * 提供错误码、消息和原因的封装。
 * </p>
 *
 * @author Xiao Lfeng &lt;xiao_lfeng@icloud.com&gt;
 * @since 1.0.0
 */
public class SsoException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final SsoErrorCode errorCode;

    /**
     * 使用错误码构造异常
     *
     * @param errorCode 错误码枚举
     */
    public SsoException(SsoErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

    /**
     * 使用错误码和自定义消息构造异常
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误消息
     */
    public SsoException(SsoErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 使用错误码和原因构造异常
     *
     * @param errorCode 错误码枚举
     * @param cause     异常原因
     */
    public SsoException(SsoErrorCode errorCode, Throwable cause) {
        super(errorCode.getDescription(), cause);
        this.errorCode = errorCode;
    }

    /**
     * 使用错误码、自定义消息和原因构造异常
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误消息
     * @param cause     异常原因
     */
    public SsoException(SsoErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * 获取错误码
     *
     * @return 错误码枚举
     */
    public SsoErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * 获取错误码字符串
     *
     * @return 错误码字符串
     */
    public String getErrorCodeString() {
        return Optional.ofNullable(errorCode)
                .map(SsoErrorCode::getCode)
                .orElse("UNKNOWN");
    }

    /**
     * 获取格式化的错误消息
     *
     * @return 格式化的错误消息，包含错误码
     */
    public String getFormattedMessage() {
        return String.format("[%s] %s", getErrorCodeString(), getMessage());
    }

    /**
     * 转换为字符串
     *
     * @return 格式化的异常信息
     */
    @Override
    public String toString() {
        String className = getClass().getName();
        String message = getLocalizedMessage();
        return String.format("%s: %s", className, message);
    }
}
