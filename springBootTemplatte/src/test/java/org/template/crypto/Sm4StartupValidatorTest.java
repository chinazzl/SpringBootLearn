package org.template.crypto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;
import org.springframework.mock.env.MockEnvironment;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

/**
 * 数据源用户名/口令解析规则单测（经 Sm4StartupValidator 覆盖 SecureDataSourceConfig 同规则）。
 * 规则：生产(sm4.enabled=true)必须双密文；本地(sm4.enabled=false)允许双明文，出现密文则拒绝。
 */
class Sm4StartupValidatorTest {

    private Sm4StartupValidator build(boolean enabled, String usernameRaw, String passwordRaw) {
        Sm4Properties properties = new Sm4Properties();
        properties.setEnabled(enabled);
        properties.setLogKeyLoaded(false);
        Sm4KeyManager keyManager;
        if (enabled) {
            keyManager = new Sm4KeyManager(
                    Collections.singletonList(() -> "validator-test-key".getBytes(StandardCharsets.UTF_8)),
                    false, false);
        } else {
            // 未启用时 KeyManager 无密钥，构造内存空实现占位（validator 未启用分支不触达 keyManager）
            keyManager = new Sm4KeyManager(
                    Collections.singletonList(() -> "unused".getBytes(StandardCharsets.UTF_8)),
                    false, false);
        }
        Sm4CipherService cipherService = new Sm4CipherService(keyManager);
        MockEnvironment env = new MockEnvironment();
        if (usernameRaw != null) {
            env.setProperty("spring.datasource.username", usernameRaw);
        }
        if (passwordRaw != null) {
            env.setProperty("spring.datasource.password", passwordRaw);
        }
        return new Sm4StartupValidator(properties, keyManager, cipherService, env);
    }

    @Test
    @DisplayName("生产：双密文通过自检")
    void productionEncryptedPass() {
        Sm4KeyManager km = new Sm4KeyManager(
                Collections.singletonList(() -> "validator-test-key".getBytes(StandardCharsets.UTF_8)),
                false, false);
        Sm4CipherService cipher = new Sm4CipherService(km);
        String userEnc = cipher.encrypt("scott");
        String passEnc = cipher.encrypt("tiger123");
        assertDoesNotThrow(() -> build(true, userEnc, passEnc).run(mock(ApplicationArguments.class)));
        km.destroy();
    }

    @Test
    @DisplayName("生产：明文口令拒绝启动")
    void productionPlainReject() {
        Sm4KeyManager km = new Sm4KeyManager(
                Collections.singletonList(() -> "validator-test-key".getBytes(StandardCharsets.UTF_8)),
                false, false);
        Sm4CipherService cipher = new Sm4CipherService(km);
        String userEnc = cipher.encrypt("scott");
        assertThrows(SmCryptoException.class,
                () -> build(true, userEnc, "plain-pass").run(mock(ApplicationArguments.class)));
        assertThrows(SmCryptoException.class,
                () -> build(true, "plain-user", userEnc).run(mock(ApplicationArguments.class)));
        km.destroy();
    }

    @Test
    @DisplayName("生产：缺用户名/口令拒绝启动")
    void productionMissingReject() {
        assertThrows(SmCryptoException.class,
                () -> build(true, null, "x").run(mock(ApplicationArguments.class)));
        assertThrows(SmCryptoException.class,
                () -> build(true, "x", "").run(mock(ApplicationArguments.class)));
    }

    @Test
    @DisplayName("本地开发：双明文放行，出现密文则拒绝")
    void localPlainPassCipherReject() {
        assertDoesNotThrow(() -> build(false, "root", "root123").run(mock(ApplicationArguments.class)));
        assertThrows(SmCryptoException.class,
                () -> build(false, "SM4(AAAA)", "root123").run(mock(ApplicationArguments.class)));
    }
}
