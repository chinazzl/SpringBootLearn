package org.template.crypto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Sm4KeyManager 单测（无密钥分片，单密钥）。
 */
class Sm4KeyManagerTest {

    @Test
    @DisplayName("空密钥材料拒绝启动")
    void emptyMaterialThrows() {
        assertThrows(SmCryptoException.class,
                () -> new Sm4KeyManager(Collections.emptyList(), false, false));
        assertThrows(SmCryptoException.class,
                () -> new Sm4KeyManager(
                        Collections.singletonList(() -> new byte[0]), false, false));
    }

    @Test
    @DisplayName("SM3 派生稳定且为 16 字节")
    void deriveStable16Bytes() {
        Sm4KeyManager m1 = new Sm4KeyManager(
                Collections.singletonList(() -> "same-material".getBytes(StandardCharsets.UTF_8)), false, false);
        Sm4KeyManager m2 = new Sm4KeyManager(
                Collections.singletonList(() -> "same-material".getBytes(StandardCharsets.UTF_8)), false, false);
        assertArrayEquals(m1.getKeyCopy(), m2.getKeyCopy());
        assertEquals(16, m1.getKeyCopy().length);
        m1.destroy();
        m2.destroy();
    }

    @Test
    @DisplayName("getKeyCopy 返回副本，外部篡改不影响常驻密钥")
    void keyCopyIsolated() {
        Sm4KeyManager manager = new Sm4KeyManager(
                Collections.singletonList(() -> "copy-test".getBytes(StandardCharsets.UTF_8)), false, false);
        byte[] copy = manager.getKeyCopy();
        byte[] before = Arrays.copyOf(copy, copy.length);
        Arrays.fill(copy, (byte) 0);
        assertArrayEquals(before, manager.getKeyCopy());
        assertNotSame(copy, manager.getKeyCopy());
        manager.destroy();
    }

    @Test
    @DisplayName("destroy 后密钥被擦除，再次获取抛异常")
    void destroyWipes() {
        Sm4KeyManager manager = new Sm4KeyManager(
                Collections.singletonList(() -> "wipe-test".getBytes(StandardCharsets.UTF_8)), false, false);
        manager.destroy();
        assertThrows(SmCryptoException.class, manager::getKeyCopy);
    }

    @Test
    @DisplayName("密钥文件加载成功（strict=false，兼容 Windows 开发机）")
    void loadFromFile(@TempDir Path dir) throws Exception {
        Path keyFile = dir.resolve("sm4.key");
        Files.write(keyFile, "file-key-material".getBytes(StandardCharsets.UTF_8));
        Sm4Properties properties = new Sm4Properties();
        properties.setEnabled(true);
        properties.setKeyFile(keyFile.toString());
        properties.setStrictFilePermission(false);
        Sm4KeyManager manager = new Sm4KeyManager(properties);
        assertTrue(manager.isLoaded());
        assertEquals(16, manager.getKeyCopy().length);
        manager.destroy();
    }

    @Test
    @DisplayName("配置了 key-file 但文件不存在则拒绝启动")
    void missingKeyFileThrows(@TempDir Path dir) {
        Sm4Properties properties = new Sm4Properties();
        properties.setEnabled(true);
        properties.setKeyFile(dir.resolve("not-exist.key").toString());
        assertThrows(SmCryptoException.class, () -> new Sm4KeyManager(properties));
    }
}
