package com.frontleaves.phalanx.beacon.sso.sdk.base.config;

import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Beacon SSO WebClient 配置类
 * <p>
 * 配置用于与 SSO 服务器通信的 OAuth WebClient，
 * 包括连接超时、读取超时等参数设置。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Configuration
public class BeaconSsoClientConfiguration {

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
     * 创建 OAuth 专用的 WebClient Bean
     * <p>
     * 配置了连接超时、读取超时以及默认请求头，
     * 用于与 SSO 服务端进行 OAuth2 协议通信。
     * </p>
     *
     * @param properties SSO 配置属性
     * @return 配置好的 WebClient 实例
     */
    @Bean("beaconSsoOAuthWebClient")
    public WebClient oauthWebClient(BeaconSsoProperties properties) {
        // 配置 HttpClient
        HttpClient httpClient = HttpClient.create()
                // 连接超时配置
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MS)
                // 响应超时配置
                .responseTimeout(Duration.ofMillis(RESPONSE_TIMEOUT_MS))
                // 读写超时配置
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(READ_TIMEOUT_MS, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS));
                });

        // 创建 WebClient
        return WebClient.builder()
                // 使用 Reactor HttpClient 连接器
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                // 设置基础 URL
                .baseUrl(properties.getBaseUrl())
                // 添加默认请求头
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "Beacon-SSO-SDK-Java/0.0.1")
                // 构建 WebClient
                .build();
    }

    /**
     * 创建用于 UserInfo 端点的 WebClient Bean
     * <p>
     * 专门用于获取用户信息的 WebClient，使用 JSON 内容类型。
     * </p>
     *
     * @param properties SSO 配置属性
     * @return 配置好的 WebClient 实例
     */
    @Bean("beaconSsoUserinfoWebClient")
    public WebClient userinfoWebClient(BeaconSsoProperties properties) {
        // 配置 HttpClient
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MS)
                .responseTimeout(Duration.ofMillis(RESPONSE_TIMEOUT_MS))
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(READ_TIMEOUT_MS, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS));
                });

        // 创建 WebClient
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "Beacon-SSO-SDK-Java/0.0.1")
                .build();
    }
}
