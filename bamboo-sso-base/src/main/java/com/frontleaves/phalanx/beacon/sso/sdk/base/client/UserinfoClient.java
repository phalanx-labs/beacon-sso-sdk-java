package com.frontleaves.phalanx.beacon.sso.sdk.base.client;

import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthUserinfo;
import reactor.core.publisher.Mono;

/**
 * 用户信息获取客户端 SPI 接口
 * <p>
 * 定义获取用户信息的统一接口，支持 gRPC 和 HTTP 双传输策略。
 * gRPC 优先 / HTTP 回退，由配置和条件注册决定实际使用的实现。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
public interface UserinfoClient {

    /**
     * 获取用户信息
     *
     * @param accessToken 访问令牌
     * @return OAuthUserinfo 用户信息
     */
    Mono<OAuthUserinfo> getUserinfo(String accessToken);
}
