package com.frontleaves.phalanx.beacon.sso.sdk.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Controller 启用/禁用配置属性类
 * <p>
 * 用于单独控制每个 Controller 的启用状态，支持灵活的模块化配置。
 * 配置前缀: {@code beacon.sso.controller}
 * </p>
 *
 * <p><b>配置示例：</b></p>
 * <pre>
 * beacon:
 *   sso:
 *     controller:
 *       auth:
 *         enabled: true
 *       user:
 *         enabled: false  # 禁用 UserController
 * </pre>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Data
@ConfigurationProperties(prefix = "beacon.sso.controller")
public class ControllerProperties {

    /**
     * AuthController (OAuth 认证) 启用配置
     */
    private ControllerSetting auth = new ControllerSetting();

    /**
     * UserController (用户信息) 启用配置
     */
    private ControllerSetting user = new ControllerSetting();

    /**
     * AccountController (账户管理) 启用配置
     */
    private ControllerSetting account = new ControllerSetting();

    /**
     * MerchantController (商户信息) 启用配置
     */
    private ControllerSetting merchant = new ControllerSetting();

    /**
     * PublicController (公开接口) 启用配置
     */
    private ControllerSetting publicInterface = new ControllerSetting();

    /**
     * Controller 启用设置
     */
    @Data
    public static class ControllerSetting {

        /**
         * 是否启用该 Controller
         * <p>
         * 默认值: {@code true}
         * </p>
         */
        private boolean enabled = true;
    }
}
