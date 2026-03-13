package com.phalanx.beacon.sso.sdk.base.properties;

import lombok.Data;

/**
 * gRPC 连接配置属性类
 * <p>
 * 定义与 SSO 服务端 gRPC 通信所需的连接参数。
 * 当需要高性能、低延迟的通信方式时可启用 gRPC。
 * </p>
 *
 * @author Xiao Lfeng
 * @since 0.0.1
 */
@Data
public class GrpcProperties {

    /**
     * gRPC 服务主机地址
     * <p>
     * 示例: {@code sso-grpc.example.com} 或 {@code localhost}
     * </p>
     */
    private String host;

    /**
     * gRPC 服务端口
     * <p>
     * 示例: {@code 9090}
     * </p>
     */
    private int port;

    /**
     * 应用访问 ID
     * <p>
     * 用于 gRPC 调用的应用身份标识
     * </p>
     */
    private String appAccessId;

    /**
     * 应用密钥
     * <p>
     * 用于 gRPC 调用签名的密钥，请妥善保管
     * </p>
     */
    private String appSecretKey;

    /**
     * 是否启用 gRPC 通信
     * <p>
     * 默认值: {@code false}
     * 当设置为 {@code true} 时，SDK 将优先使用 gRPC 进行 SSO 相关调用
     * </p>
     */
    private boolean enabled = false;
}
