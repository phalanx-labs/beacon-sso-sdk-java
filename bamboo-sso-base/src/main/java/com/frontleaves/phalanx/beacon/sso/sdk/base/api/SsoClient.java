package com.frontleaves.phalanx.beacon.sso.sdk.base.api;

import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
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
public class SsoClient {

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

    private final WebClient oauthWebClient;
    private final WebClient userinfoWebClient;

    /**
     * 构造函数
     *
     * @param properties SSO 配置属性
     */
    public SsoClient(BeaconSsoProperties properties) {
        log.debug("初始化 SsoClient, baseUrl: {}", properties.getBaseUrl());
        this.oauthWebClient = createOAuthWebClient(properties);
        this.userinfoWebClient = createUserinfoWebClient(properties);
    }

    /**
     * 获取 OAuth 专用 WebClient
     * <p>
     * 用于授权码交换、令牌刷新、令牌撤销等 OAuth 协议通信。
     * </p>
     *
     * @return OAuth 专用 WebClient
     */
    public WebClient getOAuthWebClient() {
        return oauthWebClient;
    }

    /**
     * 获取 UserInfo 专用 WebClient
     * <p>
     * 用于获取已认证用户的详细信息。
     * </p>
     *
     * @return UserInfo 专用 WebClient
     */
    public WebClient getUserinfoWebClient() {
        return userinfoWebClient;
    }

    /**
     * 创建 OAuth 专用的 WebClient
     * <p>
     * 使用 form-urlencoded 内容类型，用于 OAuth2 协议通信。
     * </p>
     *
     * @param properties SSO 配置属性
     * @return 配置好的 WebClient 实例
     */
    private WebClient createOAuthWebClient(BeaconSsoProperties properties) {
        HttpClient httpClient = createHttpClient();

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "Beacon-SSO-SDK-Java/0.0.1")
                .build();
    }

    /**
     * 创建 UserInfo 专用的 WebClient
     * <p>
     * 使用 JSON 内容类型，用于获取用户信息。
     * </p>
     *
     * @param properties SSO 配置属性
     * @return 配置好的 WebClient 实例
     */
    private WebClient createUserinfoWebClient(BeaconSsoProperties properties) {
        HttpClient httpClient = createHttpClient();

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "Beacon-SSO-SDK-Java/0.0.1")
                .build();
    }

    /**
     * 创建配置好超时参数的 HttpClient
     *
     * @return 配置好的 HttpClient 实例
     */
    private HttpClient createHttpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MS)
                .responseTimeout(Duration.ofMillis(RESPONSE_TIMEOUT_MS))
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(READ_TIMEOUT_MS, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS));
                });
    }
}
