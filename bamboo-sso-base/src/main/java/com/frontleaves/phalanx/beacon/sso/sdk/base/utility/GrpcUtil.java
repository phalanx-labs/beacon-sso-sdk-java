package com.frontleaves.phalanx.beacon.sso.sdk.base.utility;

import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoErrorCode;
import com.frontleaves.phalanx.beacon.sso.sdk.base.constant.SsoGrpcConstants;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.TokenException;
import io.grpc.Metadata;
import io.grpc.stub.AbstractStub;
import io.grpc.stub.MetadataUtils;
import org.springframework.util.StringUtils;

/**
 * gRPC 客户端公共工具类
 * <p>
 * 提供统一的 gRPC Header 附加和 Token 处理功能。
 * 使用泛型支持所有类型的 gRPC Stub。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
public final class GrpcUtil {

    // Metadata.Key 常量定义
    private static final Metadata.Key<String> APP_ACCESS_ID_KEY =
            Metadata.Key.of(SsoGrpcConstants.APP_ACCESS_ID_HEADER, Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> APP_SECRET_KEY_KEY =
            Metadata.Key.of(SsoGrpcConstants.APP_SECRET_KEY_HEADER, Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> AUTHORIZATION_KEY =
            Metadata.Key.of(SsoGrpcConstants.AUTHORIZATION_HEADER, Metadata.ASCII_STRING_MARSHALLER);

    private GrpcUtil() {
        // 工具类禁止实例化
    }

    /**
     * 附加 App 凭证到 gRPC Stub
     * <p>
     * 将应用访问 ID 和密钥添加到 gRPC 请求头中，用于服务端验证应用身份。
     * </p>
     *
     * @param stub        gRPC Stub 实例
     * @param appAccessId 应用访问 ID
     * @param appSecretKey 应用密钥
     * @param <T>         Stub 类型，必须继承自 {@link AbstractStub}
     * @return 附加了请求头的 Stub 实例
     */
    public static <T extends AbstractStub<T>> T attachAppHeaders(
            T stub,
            String appAccessId,
            String appSecretKey
    ) {
        Metadata headers = new Metadata();
        headers.put(APP_ACCESS_ID_KEY, appAccessId);
        headers.put(APP_SECRET_KEY_KEY, appSecretKey);
        return stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(headers));
    }

    /**
     * 附加 App 凭证与用户 Token 到 gRPC Stub
     * <p>
     * 将应用访问 ID、密钥以及用户授权 Token 添加到 gRPC 请求头中。
     * 用于需要用户身份验证的接口调用。
     * </p>
     *
     * @param stub        gRPC Stub 实例
     * @param appAccessId 应用访问 ID
     * @param appSecretKey 应用密钥
     * @param token       标准化后的 Bearer Token
     * @param <T>         Stub 类型，必须继承自 {@link AbstractStub}
     * @return 附加了请求头的 Stub 实例
     */
    public static <T extends AbstractStub<T>> T attachAppHeadersWithToken(
            T stub,
            String appAccessId,
            String appSecretKey,
            String token
    ) {
        Metadata headers = new Metadata();
        headers.put(APP_ACCESS_ID_KEY, appAccessId);
        headers.put(APP_SECRET_KEY_KEY, appSecretKey);
        headers.put(AUTHORIZATION_KEY, token);
        return stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(headers));
    }

    /**
     * 标准化 Access Token，确保以 Bearer 前缀开头
     * <p>
     * 处理逻辑：
     * <ol>
     *   <li>检查 Token 是否为空</li>
     *   <li>去除前后空白字符</li>
     *   <li>如果已包含 Bearer 前缀（不区分大小写），则规范化格式</li>
     *   <li>如果不包含 Bearer 前缀，则添加</li>
     * </ol>
     * </p>
     *
     * @param accessToken 原始 Access Token
     * @return 标准化后的 Bearer Token
     * @throws TokenException 当 Token 为空或无效时抛出
     */
    public static String normalizeAccessToken(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            throw TokenException.accessTokenError(SsoErrorCode.TOKEN_INVALID, "Access Token 不能为空");
        }

        String token = accessToken.trim();
        if (token.regionMatches(true, 0, SsoGrpcConstants.BEARER_PREFIX, 0, SsoGrpcConstants.BEARER_PREFIX.length())) {
            token = SsoGrpcConstants.BEARER_PREFIX + token.substring(SsoGrpcConstants.BEARER_PREFIX.length()).trim();
        } else {
            token = SsoGrpcConstants.BEARER_PREFIX + token;
        }
        return token;
    }
}
