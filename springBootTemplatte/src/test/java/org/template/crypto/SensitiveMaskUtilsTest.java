package org.template.crypto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SensitiveMaskUtils 单测：只验脱敏，不碰明文。
 */
class SensitiveMaskUtilsTest {

    @Test
    @DisplayName("口令恒掩码，不泄露原文")
    void maskPassword() {
        assertEquals("******", SensitiveMaskUtils.maskPassword("secret"));
        assertEquals("******", SensitiveMaskUtils.maskPassword("Db@Passw0rd!2026"));
        assertNull(SensitiveMaskUtils.maskPassword(null));
        assertFalse(SensitiveMaskUtils.maskPassword("secret").contains("secret"));
    }

    @Test
    @DisplayName("用户名保留首字符")
    void maskUsername() {
        assertTrue(SensitiveMaskUtils.maskUsername("admin").startsWith("a"));
        assertFalse(SensitiveMaskUtils.maskUsername("admin").contains("dmi"));
        assertEquals("*", SensitiveMaskUtils.maskUsername("a"));
        assertEquals("", SensitiveMaskUtils.maskUsername(""));
        assertNull(SensitiveMaskUtils.maskUsername(null));
    }

    @Test
    @DisplayName("JDBC URL 内嵌口令被掩盖，主机信息保留可排查")
    void maskJdbcUrl() {
        String url = "jdbc:oracle:thin:@localhost:1521:xe?user=scott&password=tiger123";
        String masked = SensitiveMaskUtils.maskJdbcUrl(url);
        assertFalse(masked.contains("tiger123"));
        assertFalse(masked.contains("scott") && masked.contains("user=scott"));
        assertTrue(masked.contains("localhost"));
        assertNull(SensitiveMaskUtils.maskJdbcUrl(null));
    }
}
