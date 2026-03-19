package com.frontleaves.phalanx.beacon.sso.sdk.base.exception;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;
import lombok.Getter;

import java.io.Serial;

/**
 * SSO 配置异常
 * <p>
 * 当 SSO 配置存在问题（如缺少必要配置、配置格式错误等）时抛出此异常。
 * 通常在应用启动时或配置验证时触发。
 * </p>
 *
 * @author Xiao Lfeng &lt;xiao_lfeng@icloud.com&gt;
 * @since 0.0.2
 */
@Getter
public class SsoConfigurationException extends SsoException {

    @Serial
    private static final long serialVersionUID = -678292224980844105L;

    /**
     * 配置项名称（可选）
     */
    private final String configKey;

    /**
     * 使用默认错误码构造异常
     */
    public SsoConfigurationException() {
        super(SsoErrorCode.CONFIGURATION_ERROR);
        this.configKey = null;
    }

    /**
     * 使用错误码构造异常
     *
     * @param errorCode 错误码枚举
     */
    public SsoConfigurationException(SsoErrorCode errorCode) {
        super(errorCode);
        this.configKey = null;
    }

    /**
     * 使用自定义消息构造异常
     *
     * @param message 自定义错误消息
     */
    public SsoConfigurationException(String message) {
        super(SsoErrorCode.CONFIGURATION_ERROR, message);
        this.configKey = null;
    }

    /**
     * 使用配置项名称和消息构造异常
     *
     * @param configKey 配置项名称
     * @param message   自定义错误消息
     */
    public SsoConfigurationException(String configKey, String message) {
        super(SsoErrorCode.CONFIGURATION_ERROR, message);
        this.configKey = configKey;
    }

    /**
     * 使用错误码、消息和配置项名称构造异常
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误消息
     * @param configKey 配置项名称
     */
    public SsoConfigurationException(SsoErrorCode errorCode, String message, String configKey) {
        super(errorCode, message);
        this.configKey = configKey;
    }

    /**
     * 使用错误码和配置项名称构造异常
     *
     * @param errorCode 错误码枚举
     * @param configKey 配置项名称
     */
    public SsoConfigurationException(SsoErrorCode errorCode, String configKey) {
        super(errorCode);
        this.configKey = configKey;
    }

    /**
     * 使用原因构造异常
     *
     * @param cause 异常原因
     */
    public SsoConfigurationException(Throwable cause) {
        super(SsoErrorCode.CONFIGURATION_ERROR, cause);
        this.configKey = null;
    }

    /**
     * 使用消息和原因构造异常
     *
     * @param message 自定义错误消息
     * @param cause   异常原因
     */
    public SsoConfigurationException(String message, Throwable cause) {
        super(SsoErrorCode.CONFIGURATION_ERROR, message, cause);
        this.configKey = null;
    }

    /**
     * 使用完整参数构造异常
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误消息
     * @param cause     异常原因
     * @param configKey 配置项名称
     */
    public SsoConfigurationException(SsoErrorCode errorCode, String message, Throwable cause, String configKey) {
        super(errorCode, message, cause);
        this.configKey = configKey;
    }

    /**
     * 创建缺少配置的异常
     *
     * @param configKey 缺少的配置项名称
     * @return SsoConfigurationException 实例
     */
    public static SsoConfigurationException missing(String configKey) {
        return new SsoConfigurationException(
                SsoErrorCode.CONFIGURATION_ERROR,
                String.format("缺少必要配置: %s", configKey),
                configKey
        );
    }

    /**
     * 创建无效配置的异常
     *
     * @param configKey 无效的配置项名称
     * @param reason    无效原因
     * @return SsoConfigurationException 实例
     */
    public static SsoConfigurationException invalid(String configKey, String reason) {
        return new SsoConfigurationException(
                SsoErrorCode.CONFIGURATION_ERROR,
                String.format("无效配置 '%s': %s", configKey, reason),
                configKey
        );
    }

    /**
     * 获取格式化的错误消息
     *
     * @return 包含配置项信息（如果有）的格式化错误消息
     */
    @Override
    public String getFormattedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(getErrorCodeString()).append("] ").append(getMessage());

        if (configKey != null) {
            sb.append(" | 配置项: ").append(configKey);
        }

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
