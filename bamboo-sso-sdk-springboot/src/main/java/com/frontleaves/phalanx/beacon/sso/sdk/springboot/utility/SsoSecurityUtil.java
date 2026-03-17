package com.frontleaves.phalanx.beacon.sso.sdk.springboot.utility;

import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthIntrospection;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthUserinfo;
import com.frontleaves.phalanx.beacon.sso.sdk.springboot.filter.BeaconSsoFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * SSO 安全工具类
 * <p>
 * 提供从 HTTP 请求中获取认证信息的便捷方法。
 * 从请求属性中获取 {@link BeaconSsoFilter} 存储的令牌和自省数据。
 * 所有方法均为静态方法，通过 final class 和 private constructor 防止实例化。
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1-SNAPSHOT
 */
public final class SsoSecurityUtil {

    /**
     * 私有构造函数，防止实例化
     */
    private SsoSecurityUtil() {
        throw new UnsupportedOperationException("工具类不能被实例化");
    }

    /**
     * 获取当前请求的 Access Token
     * <p>
     * 从请求属性中获取 {@link BeaconSsoFilter} 存储的令牌字符串。
     * </p>
     *
     * @param request HTTP 请求
     * @return 包含 Token 的 Optional，如果不存在则返回空的 Optional
     */
    public static Optional<String> getCurrentToken(HttpServletRequest request) {
        Object token = request.getAttribute(BeaconSsoFilter.ATTR_ACCESS_TOKEN);
        if (token instanceof String tokenStr && StringUtils.hasText(tokenStr)) {
            return Optional.of(tokenStr);
        }
        return Optional.empty();
    }

    /**
     * 获取当前用户信息
     * <p>
     * 基于当前请求的 Token 获取用户信息。
     * 注意：此方法需要额外调用用户信息端点，建议使用 {@link #getCurrentIntrospection} 获取基本信息。
     * </p>
     *
     * @param request HTTP 请求
     * @return 包含用户信息的 Optional，如果不存在则返回空的 Optional
     */
    public static Optional<OAuthUserinfo> getCurrentUserinfo(HttpServletRequest request) {
        // 从请求属性中获取令牌自省信息，然后构建基本用户信息
        Optional<OAuthIntrospection> introspectionOpt = getCurrentIntrospection(request);
        if (introspectionOpt.isPresent()) {
            OAuthIntrospection introspection = introspectionOpt.get();
            OAuthUserinfo userinfo = OAuthUserinfo.builder()
                    .sub(introspection.getSub())
                    .preferredUsername(introspection.getUsername())
                    .build();
            return Optional.of(userinfo);
        }
        return Optional.empty();
    }

    /**
     * 获取当前令牌自省信息
     * <p>
     * 从请求属性中获取 {@link BeaconSsoFilter} 存储的令牌自省数据。
     * 包含令牌的完整元数据信息，如用户标识、作用域、过期时间等。
     * </p>
     *
     * @param request HTTP 请求
     * @return 包含令牌自省信息的 Optional，如果不存在则返回空的 Optional
     */
    public static Optional<OAuthIntrospection> getCurrentIntrospection(HttpServletRequest request) {
        Object introspection = request.getAttribute(BeaconSsoFilter.ATTR_INTROSPECTION);
        if (introspection instanceof OAuthIntrospection oauthIntrospection) {
            return Optional.of(oauthIntrospection);
        }
        return Optional.empty();
    }

    /**
     * 检查当前请求是否已认证
     * <p>
     * 通过检查请求属性中是否存在有效的令牌自省信息来判断认证状态。
     * </p>
     *
     * @param request HTTP 请求
     * @return 如果已认证返回 true，否则返回 false
     */
    public static boolean isAuthenticated(HttpServletRequest request) {
        return getCurrentIntrospection(request)
                .map(OAuthIntrospection::isActive)
                .orElse(false);
    }

    /**
     * 检查当前用户是否有指定权限
     * <p>
     * 通过检查令牌自省信息中的作用域（scope）来判断是否具有指定权限。
     * 权限检查区分大小写。
     * </p>
     *
     * @param request    HTTP 请求
     * @param permission 需要检查的权限（作用域）
     * @return 如果具有指定权限返回 true，否则返回 false
     */
    public static boolean hasPermission(HttpServletRequest request, String permission) {
        if (!StringUtils.hasText(permission)) {
            return false;
        }

        return getCurrentIntrospection(request)
                .map(introspection -> {
                    String scope = introspection.getScope();
                    if (!StringUtils.hasText(scope)) {
                        return false;
                    }
                    // 作用域以空格分隔
                    String[] scopes = scope.split("\\s+");
                    for (String s : scopes) {
                        if (permission.equals(s)) {
                            return true;
                        }
                    }
                    return false;
                })
                .orElse(false);
    }
}
