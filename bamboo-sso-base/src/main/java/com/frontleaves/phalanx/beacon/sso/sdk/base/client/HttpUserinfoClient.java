package com.frontleaves.phalanx.beacon.sso.sdk.base.client;

import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthUserinfo;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * HTTP 用户信息客户端实现
 * <p>
 * 通过 HTTP 请求获取用户信息，委托 {@link UserApi} 执行实际的 HTTP 调用。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@RequiredArgsConstructor
public class HttpUserinfoClient implements UserinfoClient {

    private final UserApi userApi;

    @Override
    public Mono<OAuthUserinfo> getUserinfo(String accessToken) {
        return userApi.getUserinfo(accessToken);
    }
}
