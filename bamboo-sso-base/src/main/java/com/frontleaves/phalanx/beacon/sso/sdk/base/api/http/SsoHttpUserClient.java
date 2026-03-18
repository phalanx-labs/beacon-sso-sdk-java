package com.frontleaves.phalanx.beacon.sso.sdk.base.api.http;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoConfigurationException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.TokenException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.result.user.UserinfoResult;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

/**
 * SSO HTTP 用户服务客户端
 * <p>
 * 封装用户相关操作，通过 HTTP 协议与 SSO 服务通信。
 * 作为 gRPC 不可用时的回退实现。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
public class SsoHttpUserClient {

    private final BeaconSsoProperties properties;
    private final WebClient ssoWebClient;

    /**
     * 构造函数
     *
     * @param beaconSsoProperties SSO 配置属性
     * @param ssoWebClient        已配置的 WebClient 实例
     * @throws SsoConfigurationException 如果配置无效
     */
    @Contract("null, _ -> fail; !null, null -> fail")
    public SsoHttpUserClient(BeaconSsoProperties beaconSsoProperties, WebClient ssoWebClient) {
        if (beaconSsoProperties == null) {
            throw new SsoConfigurationException("SSO 配置未设置");
        }
        if (ssoWebClient == null) {
            throw new SsoConfigurationException("WebClient 未配置");
        }
        if (!StringUtils.hasText(beaconSsoProperties.getBaseUrl())) {
            throw new SsoConfigurationException("SSO 基础 URL 未配置");
        }
        if (beaconSsoProperties.getEndpoints() == null) {
            throw new SsoConfigurationException("OAuth 端点配置未设置");
        }

        this.properties = beaconSsoProperties;
        this.ssoWebClient = ssoWebClient;
    }

    /**
     * 获取用户信息
     *
     * @param accessToken 访问令牌
     * @return UserinfoResult 用户信息
     */
    public Mono<UserinfoResult> getUserinfo(String accessToken) {
        return getUserinfoSdk(accessToken);
    }

    // ========== SDK Result 方法 ==========

    /**
     * 获取用户信息（SDK Result）
     *
     * @param accessToken 访问令牌
     * @return UserinfoResult 用户信息结果
     */
    public Mono<UserinfoResult> getUserinfoSdk(String accessToken) {
        return Mono.defer(() -> {
            if (!StringUtils.hasText(accessToken)) {
                return Mono.error(new TokenException(
                        TokenException.TOKEN_TYPE_ACCESS,
                        "Access Token 不能为空"
                ));
            }

            String userinfoUrl = UriComponentsBuilder.fromUriString(properties.getBaseUrl())
                    .path(properties.getEndpoints().getUserinfoUri())
                    .build()
                    .toUriString();

            log.debug("[HTTP] 正在从以下地址获取用户信息: {}", userinfoUrl);

            return ssoWebClient
                    .get()
                    .uri(userinfoUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(UserinfoResult.class)
                    .onErrorMap(error -> {
                        log.error("[HTTP] 获取用户信息失败: {}", error.getMessage());
                        if (error instanceof TokenException) {
                            return error;
                        }
                        return new TokenException(
                                SsoErrorCode.USERINFO_FAILED,
                                "获取用户信息失败: " + error.getMessage(),
                                error,
                                TokenException.TOKEN_TYPE_ACCESS
                        );
                    });
        });
    }
}
