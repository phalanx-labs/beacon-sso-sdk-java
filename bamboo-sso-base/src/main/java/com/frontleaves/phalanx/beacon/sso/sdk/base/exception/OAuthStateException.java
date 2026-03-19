package com.frontleaves.phalanx.beacon.sso.sdk.base.exception;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;

import java.io.Serial;
import java.util.Optional;

/**
 * OAuth State 验证异常
 * <p>
 * 当 OAuth State 参数验证失败时抛出此异常。
 * 记录导致验证失败的 State 值以便于调试和日志追踪。
 * </p>
 *
 * @author Xiao Lfeng &lt;xiao_lfeng@icloud.com&gt;
 * @since 0.0.2
 */
public class OAuthStateException extends SsoException {

    @Serial
    private static final long serialVersionUID = -929935175628860180L;

    /**
     * 无效的 State 值（可选）
     */
    private final String state;

    /**
     * 使用默认错误码构造异常
     */
    public OAuthStateException() {
        super(SsoErrorCode.INVALID_STATE);
        this.state = null;
    }

    /**
     * 使用错误码构造异常
     *
     * @param errorCode 错误码枚举
     */
    public OAuthStateException(SsoErrorCode errorCode) {
        super(errorCode);
        this.state = null;
    }

    /**
     * 使用自定义消息构造异常
     *
     * @param message 自定义错误消息
     */
    public OAuthStateException(String message) {
        super(SsoErrorCode.INVALID_STATE, message);
        this.state = null;
    }

    /**
     * 使用 State 值构造异常
     *
     * @param state 无效的 State 值
     * @param message 自定义错误消息
     */
    public OAuthStateException(String state, String message) {
        super(SsoErrorCode.INVALID_STATE, message);
        this.state = state;
    }

    /**
     * 使用错误码和 State 值构造异常
     *
     * @param errorCode 错误码枚举
     * @param state     无效的 State 值
     */
    public OAuthStateException(SsoErrorCode errorCode, String state) {
        super(errorCode);
        this.state = state;
    }

    /**
     * 使用错误码、消息和 State 值构造异常
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误消息
     * @param state     无效的 State 值
     */
    public OAuthStateException(SsoErrorCode errorCode, String message, String state) {
        super(errorCode, message);
        this.state = state;
    }

    /**
     * 使用原因构造异常
     *
     * @param cause 异常原因
     */
    public OAuthStateException(Throwable cause) {
        super(SsoErrorCode.INVALID_STATE, cause);
        this.state = null;
    }

    /**
     * 使用消息和原因构造异常
     *
     * @param message 自定义错误消息
     * @param cause   异常原因
     */
    public OAuthStateException(String message, Throwable cause) {
        super(SsoErrorCode.INVALID_STATE, message, cause);
        this.state = null;
    }

    /**
     * 使用完整参数构造异常
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误消息
     * @param cause     异常原因
     * @param state     无效的 State 值
     */
    public OAuthStateException(SsoErrorCode errorCode, String message, Throwable cause, String state) {
        super(errorCode, message, cause);
        this.state = state;
    }

    /**
     * 获取无效的 State 值
     *
     * @return State 值的 {@link Optional}，如果未设置则返回空
     */
    public Optional<String> getState() {
        return Optional.ofNullable(state);
    }

    /**
     * 获取格式化的错误消息
     *
     * @return 包含 State 信息（如果有）的格式化错误消息
     */
    @Override
    public String getFormattedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(getErrorCodeString()).append("] ").append(getMessage());

        getState().ifPresent(s -> sb.append(" | 状态值: ").append(s));

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
