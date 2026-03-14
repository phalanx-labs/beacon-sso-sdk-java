package com.frontleaves.phalanx.beacon.sso.sdk.base.utility;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

/**
 * OAuth State 工具类
 * <p>
 * 提供 OAuth 2.0 授权流程中 state 参数的生成与验证功能。
 * state 参数用于防止 CSRF (跨站请求伪造) 攻击。
 * </p>
 *
 * @author Xiao Lfeng &lt;xiao_lfeng@icloud.com&gt;
 * @since 1.0.0
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.1">RFC 6749 - State Parameter</a>
 */
public final class StateUtil {

    /**
     * 默认 state 长度 (32 字节 = 43 个 Base64 字符)
     */
    private static final int DEFAULT_STATE_BYTE_LENGTH = 32;

    /**
     * 最小 state 长度 (16 字节)
     */
    private static final int MIN_STATE_BYTE_LENGTH = 16;

    /**
     * 最大 state 长度 (64 字节)
     */
    private static final int MAX_STATE_BYTE_LENGTH = 64;

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
    private StateUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 生成默认长度的随机 state 字符串
     * <p>
     * 生成一个使用安全随机数的 Base64 URL 安全编码的 state 字符串。
     * 默认长度为 32 字节，编码后约 43 个字符。
     * </p>
     *
     * @return 随机 state 字符串
     */
    public static String generateState() {
        return generateState(DEFAULT_STATE_BYTE_LENGTH);
    }

    /**
     * 生成指定字节长度的随机 state 字符串
     * <p>
     * 字节长度会被限制在 16-64 字节范围内以确保安全性。
     * </p>
     *
     * @param byteLength 期望的字节长度 (16-64)
     * @return 随机 state 字符串
     * @throws IllegalArgumentException 如果字节长度不在有效范围内
     */
    public static String generateState(int byteLength) {
        int validLength = Math.max(MIN_STATE_BYTE_LENGTH, Math.min(byteLength, MAX_STATE_BYTE_LENGTH));

        byte[] randomBytes = new byte[validLength];
        SECURE_RANDOM.nextBytes(randomBytes);

        return BASE64_URL_ENCODER.encodeToString(randomBytes);
    }

    /**
     * 验证 state 是否匹配
     * <p>
     * 使用常量时间比较，防止时序攻击。
     * </p>
     *
     * @param expected 预期的 state (存储在会话中的值)
     * @param actual   实际接收到的 state (回调中的值)
     * @return 如果匹配返回 {@code true}，否则返回 {@code false}
     */
    public static boolean validateState(String expected, String actual) {
        // 使用 Optional 优雅处理 null 情况
        return Optional.ofNullable(expected)
                .flatMap(exp -> Optional.ofNullable(actual))
                .map(act -> constantTimeEquals(expected, actual))
                .orElse(false);
    }

    /**
     * 验证 state 并在失败时抛出异常
     * <p>
     * 当 state 不匹配时抛出 {@link StateValidationException}。
     * </p>
     *
     * @param expected 预期的 state
     * @param actual   实际接收到的 state
     * @throws StateValidationException 如果 state 不匹配
     */
    public static void validateStateOrThrow(String expected, String actual) {
        if (!validateState(expected, actual)) {
            throw new StateValidationException(
                    expected != null ? maskState(expected) : "null",
                    actual != null ? maskState(actual) : "null"
            );
        }
    }

    /**
     * 常量时间字符串比较
     * <p>
     * 使用常量时间比较算法，防止时序攻击。
     * </p>
     *
     * @param a 第一个字符串
     * @param b 第二个字符串
     * @return 如果相等返回 {@code true}，否则返回 {@code false}
     */
    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }

        byte[] aBytes = a.getBytes(StandardCharsets.US_ASCII);
        byte[] bBytes = b.getBytes(StandardCharsets.US_ASCII);

        int result = 0;
        for (int i = 0; i < aBytes.length; i++) {
            result |= aBytes[i] ^ bBytes[i];
        }

        return result == 0;
    }

    /**
     * 遮蔽 state 字符串用于日志输出
     * <p>
     * 只显示前 4 个和后 4 个字符，中间用 * 替代。
     * </p>
     *
     * @param state 原始 state 字符串
     * @return 遮蔽后的 state 字符串
     */
    private static String maskState(String state) {
        if (state == null) {
            return "null";
        }

        if (state.length() <= 8) {
            return "*".repeat(state.length());
        }

        int visibleChars = 4;
        String prefix = state.substring(0, visibleChars);
        String suffix = state.substring(state.length() - visibleChars);
        String masked = "*".repeat(state.length() - 2 * visibleChars);

        return prefix + masked + suffix;
    }

    /**
     * State 验证异常
     * <p>
     * 当 OAuth state 参数验证失败时抛出此异常。
     * </p>
     */
    public static class StateValidationException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        /**
         * 预期的 state (已遮蔽)
         */
        private final String expectedState;

        /**
         * 实际的 state (已遮蔽)
         */
        private final String actualState;

        /**
         * 构造 State 验证异常
         *
         * @param expectedState 预期的 state (已遮蔽)
         * @param actualState   实际的 state (已遮蔽)
         */
        public StateValidationException(String expectedState, String actualState) {
            super(String.format("State validation failed: expected [%s], but got [%s]",
                    expectedState, actualState));
            this.expectedState = expectedState;
            this.actualState = actualState;
        }

        /**
         * 获取预期的 state (已遮蔽)
         *
         * @return 预期的 state
         */
        public String getExpectedState() {
            return expectedState;
        }

        /**
         * 获取实际的 state (已遮蔽)
         *
         * @return 实际的 state
         */
        public String getActualState() {
            return actualState;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StateValidationException that = (StateValidationException) o;
            return Objects.equals(expectedState, that.expectedState)
                    && Objects.equals(actualState, that.actualState);
        }

        @Override
        public int hashCode() {
            return Objects.hash(expectedState, actualState);
        }
    }
}
