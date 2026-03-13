package com.phalanx.beacon.sso.sdk.base.utility;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

/**
 * PKCE (Proof Key for Code Exchange) 工具类
 * <p>
 * 提供 PKCE 流程中所需的 code_verifier 和 code_challenge 的生成与验证功能。
 * PKCE 用于增强 OAuth 2.0 授权码流程的安全性，防止授权码拦截攻击。
 * </p>
 *
 * @author Xiao Lfeng &lt;xiao_lfeng@icloud.com&gt;
 * @since 1.0.0
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7636">RFC 7636 - PKCE</a>
 */
public final class PkceUtil {

    /**
     * code_verifier 最小长度 (43 字符 = 256 位熵)
     */
    private static final int CODE_VERIFIER_MIN_LENGTH = 43;

    /**
     * code_verifier 最大长度 (128 字符)
     */
    private static final int CODE_VERIFIER_MAX_LENGTH = 128;

    /**
     * 默认 code_verifier 长度 (43 字符，提供足够的安全性)
     */
    private static final int DEFAULT_CODE_VERIFIER_LENGTH = 43;

    /**
     * 安全随机数生成器
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Base64 URL 编码器（无填充）
     */
    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

    /**
     * 私有构造函数，防止实例化
     */
    private PkceUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 生成默认长度 (43 字符) 的 code_verifier
     * <p>
     * code_verifier 是一个使用安全随机数生成的高熵加密随机字符串，
     * 使用 Base64 URL 安全编码（无填充）。
     * </p>
     *
     * @return code_verifier 字符串 (43 字符)
     */
    public static String generateCodeVerifier() {
        return generateCodeVerifier(DEFAULT_CODE_VERIFIER_LENGTH);
    }

    /**
     * 生成指定长度的 code_verifier
     * <p>
     * 根据 RFC 7636 规范，code_verifier 长度必须在 43-128 字符之间。
     * </p>
     *
     * @param length 期望的 code_verifier 长度 (43-128)
     * @return code_verifier 字符串
     * @throws IllegalArgumentException 如果长度不在有效范围内
     */
    public static String generateCodeVerifier(int length) {
        if (length < CODE_VERIFIER_MIN_LENGTH || length > CODE_VERIFIER_MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Code verifier length must be between %d and %d, but was: %d",
                            CODE_VERIFIER_MIN_LENGTH, CODE_VERIFIER_MAX_LENGTH, length)
            );
        }

        // 计算需要的字节数 (Base64 编码后长度约为字节数的 4/3)
        // 对于 43 字符，需要 32 字节；对于 128 字符，需要 96 字节
        int byteLength = (int) Math.ceil(length * 3.0 / 4.0);
        byte[] randomBytes = new byte[byteLength];
        SECURE_RANDOM.nextBytes(randomBytes);

        String codeVerifier = BASE64_URL_ENCODER.encodeToString(randomBytes);

        // 截取到指定长度
        return codeVerifier.substring(0, length);
    }

    /**
     * 从 code_verifier 生成 code_challenge
     * <p>
     * 使用 SHA-256 对 code_verifier 进行哈希，然后使用 Base64 URL 安全编码（无填充）。
     * 这是 PKCE 的 S256 转换方法。
     * </p>
     *
     * @param codeVerifier code_verifier 字符串
     * @return code_challenge 字符串
     * @throws IllegalArgumentException 如果 codeVerifier 为 null 或空
     */
    public static String generateCodeChallenge(String codeVerifier) {
        if (codeVerifier == null || codeVerifier.isEmpty()) {
            throw new IllegalArgumentException("Code verifier cannot be null or empty");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
            return BASE64_URL_ENCODER.encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 在所有 Java 实现中都必须可用
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * 验证 code_verifier 与 code_challenge 是否匹配
     * <p>
     * 对 code_verifier 重新计算 code_challenge，并与提供的 code_challenge 比较。
     * </p>
     *
     * @param codeVerifier  待验证的 code_verifier
     * @param codeChallenge 预期的 code_challenge
     * @return 如果匹配返回 {@code true}，否则返回 {@code false}
     */
    public static boolean verifyCodeChallenge(String codeVerifier, String codeChallenge) {
        // 使用 Optional 优雅处理 null 情况
        return Optional.ofNullable(codeVerifier)
                .filter(verifier -> !verifier.isEmpty())
                .flatMap(verifier -> Optional.ofNullable(codeChallenge))
                .map(challenge -> {
                    String computedChallenge = generateCodeChallenge(codeVerifier);
                    return MessageDigest.isEqual(
                            computedChallenge.getBytes(StandardCharsets.US_ASCII),
                            challenge.getBytes(StandardCharsets.US_ASCII)
                    );
                })
                .orElse(false);
    }

    /**
     * 获取 PKCE 使用的转换方法
     *
     * @return 转换方法标识 ("S256")
     */
    public static String getCodeChallengeMethod() {
        return "S256";
    }
}
