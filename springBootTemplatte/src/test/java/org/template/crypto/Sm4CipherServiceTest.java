package org.template.crypto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 国密 SM4 加解密单元测试。
 *
 * @author security
 */
class Sm4CipherServiceTest {

    private Sm4CipherService cipherService;

    @BeforeEach
    void setUp() {
        // 测试用固定密钥材料（仅存在于测试用例中，生产环境密钥来自环境变量/密钥文件）
        Sm4KeyManager keyManager = new Sm4KeyManager(
                Collections.singletonList(() -> "unit-test-key-material".getBytes(java.nio.charset.StandardCharsets.UTF_8)),
                false, false);
        cipherService = new Sm4CipherService(keyManager);
    }

    @Test
    @DisplayName("SM4 加解密可逆")
    void encryptDecrypt() {
        String plain = "Db@Passw0rd!2026";
        String cipher = cipherService.encrypt(plain);
        assertTrue(cipher.startsWith("SM4("));
        assertTrue(cipher.endsWith(")"));
        assertNotEquals(plain, cipher);
        assertEquals(plain, cipherService.decrypt(cipher));
    }

    @Test
    @DisplayName("同一明文两次加密结果不同(随机 IV)")
    void randomIv() {
        String plain = "same-password";
        assertNotEquals(cipherService.encrypt(plain), cipherService.encrypt(plain));
    }

    @Test
    @DisplayName("解密后 char[] 可被擦除")
    void wipeChars() {
        String cipher = cipherService.encrypt("wipe-me");
        char[] chars = cipherService.decryptToChars(cipher);
        assertArrayEquals("wipe-me".toCharArray(), chars);
        Arrays.fill(chars, '\0');
        assertArrayEquals(new char[] {'\0', '\0', '\0', '\0', '\0', '\0', '\0'}, chars);
    }

    @Test
    @DisplayName("密文被篡改时解密失败")
    void tamperedCipher() {
        String cipher = cipherService.encrypt("tamper-check");
        String body = cipher.substring(4, cipher.length() - 1);
        char first = body.charAt(0);
        body = (first == 'A' ? 'B' : 'A') + body.substring(1);
        try {
            cipherService.decrypt("SM4(" + body + ")");
            // BC GCM 标签校验失败会抛异常，此处若未抛异常说明实现有问题
            org.junit.jupiter.api.Assertions.fail("篡改后的密文应解密失败");
        } catch (SmCryptoException expected) {
            assertFalse(expected.getMessage() == null && expected.getMessage().contains("tamper-check"));
        }
    }

    @Test
    @DisplayName("非 SM4 前缀的值原样返回")
    void plainValue() {
        assertFalse(cipherService.isEncrypted("plain-text"));
        assertEquals("plain-text", cipherService.decrypt("plain-text"));
    }

    @Test
    @DisplayName("敏感信息脱敏不泄露明文")
    void mask() {
        assertFalse(SensitiveMaskUtils.maskPassword("secret").contains("secret"));
        assertFalse(SensitiveMaskUtils.maskUsername("admin").contains("dmi"));
        assertTrue(SensitiveMaskUtils.maskUsername("admin").startsWith("a"));
    }
}
