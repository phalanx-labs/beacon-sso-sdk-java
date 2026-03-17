package com.frontleaves.phalanx.beacon.sso.sdk.base.api;

import com.frontleaves.phalanx.beacon.sso.sdk.base.client.SsoRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.SsoConfigurationException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.TokenException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthUserinfo;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.SsoUserDetail;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.GrpcModelConverter;
import com.frontleaves.phalanx.beacon.sso.sdk.base.utility.GrpcUserConverter;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.GetUserByIDRequest;
import com.frontleaves.phalanx.beacon.sso.sdk.grpc.v1.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

/**
 * 用户操作（双传输：gRPC 优先，HTTP 回退）
 * <p>
 * 封装用户相关操作，支持 gRPC 和 HTTP 双传输。
 * 当 gRPC 启用时优先使用 gRPC，否则回退到 HTTP。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RequiredArgsConstructor
public class SsoUserApi {

    private final BeaconSsoProperties properties;
    private final SsoClient ssoClient;
    private final SsoRequest ssoRequest;
    private final GrpcUserConverter userConverter;
    private final GrpcModelConverter modelConverter;

    /**
     * 获取当前用户信息（双传输：gRPC 优先，HTTP 回退）
     *
     * @param accessToken 访问令牌
     * @return OAuthUserinfo 用户信息
     */
    public Mono<OAuthUserinfo> getCurrentUser(String accessToken) {
        if (isGrpcEnabled()) {
            log.debug("使用 gRPC 获取当前用户信息");
            return Mono.fromCallable(() -> {
                User user = ssoRequest.user().getCurrentUser(accessToken);
                return userConverter.convert(user);
            });
        }
        // HTTP 回退
        log.debug("使用 HTTP 获取当前用户信息");
        return httpGetCurrentUser(accessToken);
    }

    /**
     * 根据用户 ID 获取详细信息（gRPC-only）
     * <p>
     * 此方法仅支持 gRPC 通信，未启用 gRPC 时将抛出异常。
     * </p>
     *
     * @param accessToken 访问令牌
     * @param request     用户查询请求
     * @return SsoUserDetail 用户详细信息
     * @throws SsoConfigurationException 如果 gRPC 未启用
     */
    public SsoUserDetail getUserById(String accessToken, GetUserByIDRequest request) {
        if (!isGrpcEnabled()) {
            throw new SsoConfigurationException(
                    SsoErrorCode.GRPC_NOT_ENABLED,
                    "getUserById 需要 gRPC 通信，请启用 gRPC 配置"
            );
        }
        log.debug("使用 gRPC 根据 ID 获取用户信息: userId={}", request.getUserId());
        User user = ssoRequest.user().getUserById(accessToken, request);
        return modelConverter.toUserDetail(user);
    }

    /**
     * 判断 gRPC 是否启用
     *
     * @return 如果 gRPC 启用返回 true，否则返回 false
     */
    private boolean isGrpcEnabled() {
        return ssoRequest != null
                && properties.getGrpc() != null
                && properties.getGrpc().isEnabled();
    }

    /**
     * 通过 HTTP 获取当前用户信息
     *
     * @param accessToken 访问令牌
     * @return OAuthUserinfo 用户信息
     */
    private Mono<OAuthUserinfo> httpGetCurrentUser(String accessToken) {
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
