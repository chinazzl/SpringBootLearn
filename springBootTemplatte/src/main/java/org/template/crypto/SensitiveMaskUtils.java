package org.template.crypto;

/**
 * 用户名 / 口令日志脱敏工具。
 *
 * <p>满足监管第 2 条：用户名、密码等配置信息仅在内存中存在，不允许打印在日志中。
 * 所有日志、异常信息、toString 中涉及用户名/口令时必须经本工具脱敏。
 */
public final class SensitiveMaskUtils {

    private SensitiveMaskUtils() {
    }

    /**
     * 口令脱敏：恒返回固定掩码，不泄露任何原文片段。
     *
     * @param password 原口令（可为 null / 明文 / 密文，均不回显）
     * @return null 输入返回 null，否则返回 "******"
     */
    public static String maskPassword(String password) {
        if (password == null) {
            return null;
        }
        return "******";
    }

    /**
     * 用户名脱敏：保留首字符，其余掩码，便于定位问题又不泄露全名。
     * 例：admin -&gt; a***。
     *
     * @param username 用户名
     * @return 脱敏后用户名
     */
    public static String maskUsername(String username) {
        if (username == null) {
            return null;
        }
        if (username.isEmpty()) {
            return "";
        }
        if (username.length() == 1) {
            return "*";
        }
        return username.charAt(0) + "***";
    }

    /**
     * JDBC URL 脱敏：URL 本身不加密，但防止 url 中携带 user/password 参数被直接打印。
     * 将 {@code password=xxx} / {@code user=xxx} 片段替换为掩码。
     */
    public static String maskJdbcUrl(String url) {
        if (url == null) {
            return null;
        }
        // 处理 oracle/mysql 常见的内嵌口令写法
        String masked = url.replaceAll("(?i)(password=)[^;&\\s]*", "$1******");
        masked = masked.replaceAll("(?i)(user=)[^;&\\s]*", "$1***");
        return masked;
    }
}
