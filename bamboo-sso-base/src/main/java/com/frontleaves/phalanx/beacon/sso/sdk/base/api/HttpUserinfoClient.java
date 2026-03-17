package com.frontleaves.phalanx.beacon.sso.sdk.base.api;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.TokenException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthUserinfo;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

/**
 * HTTP 用户信息客户端实现
 * <p>
 * 通过 HTTP 请求获取用户信息，使用 {@link SsoClient} 的 userinfoWebClient。
 * 作为 gRPC 不可用时的回退实现。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RequiredArgsConstructor
public class HttpUserinfoClient implements UserinfoClient {

    private final BeaconSsoProperties properties;
    private final SsoClient ssoClient;

    @Override
    public Mono<OAuthUserinfo> getUserinfo(String accessToken) {
        return Mono.defer(() -> {
            if (!StringUtils.hasText(accessToken)) {
                return Mono.error(new TokenException(
                        TokenException.TOKEN_TYPE_ACCESS,
                        "Access Token 不能为空"
                ));
            }

            String userinfoUrl = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                    .path(properties.getEndpoints().getUserinfoUri())
                    .build()
                    .toUriString();

            log.debug("正在从以下地址获取用户信息（HTTP）: {}", userinfoUrl);

            WebClient webClient = ssoClient.getUserinfoWebClient();

            return webClient
                    .get()
                    .uri(userinfoUrl)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Accept", "application/json")
                    .retrieve()
                    .bodyToMono(OAuthUserinfo.class)
                    .onErrorMap(error -> {
                        log.error("获取用户信息失败: {}", error.getMessage());
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
