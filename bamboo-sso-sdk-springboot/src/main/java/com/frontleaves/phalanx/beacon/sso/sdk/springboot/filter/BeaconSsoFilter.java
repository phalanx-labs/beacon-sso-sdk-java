package com.frontleaves.phalanx.beacon.sso.sdk.springboot.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontleaves.phalanx.beacon.sso.sdk.base.exception.TokenException;
import com.frontleaves.phalanx.beacon.sso.sdk.base.logic.BusinessLogic;
import com.frontleaves.phalanx.beacon.sso.sdk.base.models.OAuthIntrospection;
import com.frontleaves.phalanx.beacon.sso.sdk.base.properties.BeaconSsoProperties;
import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Beacon SSO 认证过滤器
 * <p>
 * 继承 {@link OncePerRequestFilter}，确保每个请求只经过一次过滤。
 * 负责从请求头提取 Bearer Token，调用令牌自省接口验证有效性，
 * 并将用户信息存入请求属性供后续使用。
 * </p>
 *
 * @author Xiao Lfeng
 * @since 0.0.1
 */
@Slf4j
@RequiredArgsConstructor
public class BeaconSsoFilter extends OncePerRequestFilter {

    /**
     * 请求属性键：令牌自省信息
     */
    public static final String ATTR_INTROSPECTION = "beacon.sso.introspection";

    /**
     * 请求属性键：访问令牌
     */
    public static final String ATTR_ACCESS_TOKEN = "beacon.sso.access_token";

    /**
     * Authorization 请求头名称
     */
    private static final String HEADER_AUTHORIZATION = "Authorization";

    /**
     * Bearer Token 前缀
     */
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * 业务逻辑处理（响应式）
     */
    private final BusinessLogic businessLogic;

    /**
     * SSO 配置属性
     */
    private final BeaconSsoProperties properties;

    /**
     * JSON 序列化工具
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Ant 路径匹配器
     */
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // 1. 从请求头获取 Bearer Token
        String token = extractBearerToken(request);

        if (!StringUtils.hasText(token)) {
            log.debug("请求 {} 中未找到 Bearer Token", request.getRequestURI());
            writeUnauthorizedResponse(response, "缺少或无效的 Authorization 请求头");
            return;
        }

        try {
            // 2. 调用令牌自省接口验证 Token
            OAuthIntrospection introspection = businessLogic
                    .introspectToken(token)
                    .block();

            // 3. 检查令牌是否有效
            if (introspection == null || !introspection.isActive()) {
                log.debug("请求 {} 的令牌不活跃", request.getRequestURI());
                writeUnauthorizedResponse(response, "令牌无效或已过期");
                return;
            }

            // 4. 将用户信息存入请求属性
            request.setAttribute(ATTR_INTROSPECTION, introspection);
            request.setAttribute(ATTR_ACCESS_TOKEN, token);

            log.debug("用户 {} 的令牌验证成功", introspection.getSub());

            // 5. 继续过滤链
            filterChain.doFilter(request, response);

        } catch (TokenException e) {
            log.warn("令牌验证失败: {}", e.getFormattedMessage());
            writeUnauthorizedResponse(response, e.getMessage());
        } catch (Exception e) {
            log.error("令牌验证过程中发生意外错误", e);
            writeUnauthorizedResponse(response, "认证失败");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        List<String> excludeUrls = properties.getExcludeUrls();
        if (excludeUrls == null || excludeUrls.isEmpty()) {
            return false;
        }

        String requestPath = request.getRequestURI();
        return excludeUrls.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestPath));
    }

    /**
     * 从请求头提取 Bearer Token
     *
     * @param request HTTP 请求
     * @return Token 字符串，如果不存在或格式错误则返回 null
     */
    private String extractBearerToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HEADER_AUTHORIZATION))
                .filter(header -> header.startsWith(BEARER_PREFIX))
                .map(header -> header.substring(BEARER_PREFIX.length()))
                .filter(StringUtils::hasText)
                .orElse(null);
    }

    /**
     * 写入 401 未授权响应
     *
     * @param response HTTP 响应
     * @param message  错误消息
     */
    private void writeUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        BaseResponse<Void> errorResponse = new BaseResponse<>(
                ErrorCode.UNAUTHORIZED.getOutput(),
                ErrorCode.UNAUTHORIZED.getCode(),
                ErrorCode.UNAUTHORIZED.getMessage(),
                message,
                null
        );

        String responseBody = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(responseBody);
        response.getWriter().flush();
    }
}
