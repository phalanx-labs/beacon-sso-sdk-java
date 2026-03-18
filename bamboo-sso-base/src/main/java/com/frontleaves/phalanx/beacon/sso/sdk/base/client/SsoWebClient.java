package com.frontleaves.phalanx.beacon.sso.sdk.base.client;

import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * SSO 统一 HTTP 客户端封装
 * <p>
 * 封装 SSO SDK 所需的 WebClient 实例，提供 OAuth 和 UserInfo 两种专用的 WebClient。
 * 内部管理连接超时、读取超时等配置，对外提供类型安全的访问接口。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
public class SsoWebClient {

    /**
     * 连接超时时间（毫秒）
     */
    private static final int CONNECT_TIMEOUT_MS = 10_000;

    /**
     * 读取超时时间（毫秒）
     */
    private static final int READ_TIMEOUT_MS = 30_000;

    /**
     * 写入超时时间（毫秒）
     */
    private static final int WRITE_TIMEOUT_MS = 10_000;

    /**
     * 响应超时时间（毫秒）
     */
    private static final long RESPONSE_TIMEOUT_MS = 60_000;
    /**
     * 构造函数
     *
     * @param properties SSO 配置属性
     */
    public SsoWebClient(@NonNull BeaconSsoProperties properties) {
        log.debug("初始化 SsoWebClient, baseUrl: {}", properties.getBaseUrl());
    }

    /**
     * 创建并配置一个用于 SSO 通信的 WebClient 实例。
     * 内部自动配置了连接超时、读取超时、写入超时及响应超时，
     * 并预设了 Content-Type、Accept 及 User-Agent 等默认请求头。
     *
     * @param properties SSO 配置属性，用于获取 SSO 服务器的基础 URL
     * @return 配置完成的 WebClient 实例
     */
    public @NotNull WebClient createWebClient(@NonNull BeaconSsoProperties properties) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MS)
                .responseTimeout(Duration.ofMillis(RESPONSE_TIMEOUT_MS))
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(READ_TIMEOUT_MS, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS));
                });

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "Beacon-SSO-SDK-Java/0.0.1")
                .build();
    }
}
