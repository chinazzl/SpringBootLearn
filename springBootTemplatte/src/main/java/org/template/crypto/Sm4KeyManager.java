package org.template.crypto;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * SM4 单密钥管理器（无密钥分片）。
 *
 * <p>密钥材料只允许来自：
 * <ul>
 *   <li>环境变量（ {@link Sm4Properties#getKeyEnv()}，默认 {@code SM4_KEY}）；</li>
 *   <li>密钥文件（ {@link Sm4Properties#getKeyFile()}，内容即密钥材料，权限必须 600）。</li>
 * </ul>
 * 优先级：keyFile 非空且文件存在时优先使用文件，否则使用环境变量。
 * 密钥材料经 SM3 摘要取前 16 字节派生为 SM4-128 密钥，派生后立即擦除原始材料。
 */
@Component
@Slf4j
public class Sm4KeyManager implements DisposableBean {


    private final Sm4Properties properties;
    private final boolean strictFilePermission;
    private final boolean logKeyLoaded;

    /** 派生后的 16 字节 SM4 密钥，常驻内存，destroy 时擦除 */
    private volatile byte[] sm4Key;

    /** Spring 构造：按 Sm4Properties 加载单密钥 */
    @Autowired
    public Sm4KeyManager(Sm4Properties properties) {
        this.properties = properties;
        this.strictFilePermission = properties.isStrictFilePermission();
        this.logKeyLoaded = properties.isLogKeyLoaded();
        if (properties.isEnabled()) {
            this.sm4Key = loadAndDerive();
        }
    }

    /**
     * 测试 / 兼容构造：直接给密钥材料（内存传入，不走环境变量与文件）。
     * 保留该构造仅为兼容既有单测 {@code Sm4CipherServiceTest}，生产代码请走
     * {@link #Sm4KeyManager(Sm4Properties)}。
     *
     * @param keyMaterials 密钥材料供给者列表（单密钥场景传 singletonList 即可，多给则按序拼接）
     * @param strictFilePermission 占位（内存模式无文件校验）
     * @param logKeyLoaded 是否打印“密钥已加载”
     */
    public Sm4KeyManager(List<Supplier<byte[]>> keyMaterials, boolean strictFilePermission, boolean logKeyLoaded) {
        this.properties = null;
        this.strictFilePermission = strictFilePermission;
        this.logKeyLoaded = logKeyLoaded;
        if (keyMaterials == null || keyMaterials.isEmpty()) {
            throw new SmCryptoException("SM4 密钥材料为空，拒绝启动");
        }
        List<byte[]> parts = new ArrayList<>();
        try {
            for (Supplier<byte[]> s : keyMaterials) {
                byte[] b = s == null ? null : s.get();
                if (b == null || b.length == 0) {
                    throw new SmCryptoException("SM4 密钥材料为空，拒绝启动");
                }
                parts.add(b);
            }
            this.sm4Key = deriveSm4Key(concat(parts));
        } finally {
            for (byte[] b : parts) {
                Arrays.fill(b, (byte) 0);
            }
        }
        if (logKeyLoaded) {
            log.info("SM4 密钥已加载（测试/内存模式）");
        }
    }

    private byte[] loadAndDerive() {
        byte[] material = loadMaterial();
        try {
            if (material == null || material.length == 0) {
                throw new SmCryptoException(
                        "未找到 SM4 密钥材料：请设置环境变量 [" + properties.getKeyEnv() + "] 或配置 sm4.key-file，拒绝启动");
            }
            return deriveSm4Key(material);
        } finally {
            if (material != null) {
                Arrays.fill(material, (byte) 0);
            }
        }
    }

    private byte[] loadMaterial() {
        // 1) 密钥文件优先
        String keyFile = properties.getKeyFile();
        if (keyFile != null && !keyFile.trim().isEmpty()) {
            Path path = Paths.get(keyFile.trim());
            if (Files.isRegularFile(path)) {
                checkFilePermission(path);
                try {
                    byte[] raw = Files.readAllBytes(path);
                    byte[] trimmed = trim(raw);
                    Arrays.fill(raw, (byte) 0);
                    if (trimmed.length == 0) {
                        throw new SmCryptoException("SM4 密钥文件为空：[" + path + "]，拒绝启动");
                    }
                    return trimmed;
                } catch (IOException e) {
                    throw new SmCryptoException("读取 SM4 密钥文件失败：[" + path + "]，拒绝启动", e);
                }
            }
            // 配置了 keyFile 但文件不存在：直接拒绝，避免静默降级用错密钥
            throw new SmCryptoException("SM4 密钥文件不存在：[" + path + "]，拒绝启动");
        }
        // 2) 环境变量
        String envName = properties.getKeyEnv();
        String value = envName == null ? null : System.getenv(envName);
        if (value == null || value.isEmpty()) {
            return null;
        }
        return value.getBytes(StandardCharsets.UTF_8);
    }

    private void checkFilePermission(Path path) {
        if (!strictFilePermission) {
            return;
        }
        try {
            Set<PosixFilePermission> perms = Files.getPosixFilePermissions(path);
            boolean ok = perms.size() == 2
                    && perms.contains(PosixFilePermission.OWNER_READ)
                    && perms.contains(PosixFilePermission.OWNER_WRITE);
            if (!ok) {
                throw new SmCryptoException(
                        "SM4 密钥文件权限不满足最小权限原则（要求 600，仅属主可读写）：[" + path + "] 实际=" + perms + "，拒绝启动");
            }
        } catch (UnsupportedOperationException | IOException e) {
            // Windows 等非 POSIX 文件系统无法校验：开发环境放行并告警，生产 Linux 必须满足 600
            log.warn("无法校验密钥文件 POSIX 权限（非 POSIX 文件系统），跳过强校验：[{}]", path);
        }
    }

    private static byte[] trim(byte[] raw) {
        int end = raw.length;
        while (end > 0 && (raw[end - 1] == '\r' || raw[end - 1] == '\n' || raw[end - 1] == ' ' || raw[end - 1] == '\t')) {
            end--;
        }
        int start = 0;
        while (start < end && (raw[start] == ' ' || raw[start] == '\t')) {
            start++;
        }
        return Arrays.copyOfRange(raw, start, end);
    }

    private static byte[] concat(List<byte[]> parts) {
        int len = 0;
        for (byte[] b : parts) {
            len += b.length;
        }
        byte[] out = new byte[len];
        int pos = 0;
        for (byte[] b : parts) {
            System.arraycopy(b, 0, out, pos, b.length);
            pos += b.length;
        }
        return out;
    }

    /**
     * SM3(密钥材料) 取前 16 字节作为 SM4-128 密钥。
     * 任意长度材料均可，避免“密钥必须是 16 字节可打印字符”的弱约束。
     *
     * <p>确定性函数：相同材料必然得到相同密钥（这是数据可解密的前提）。
     *
     * <p><b>所有权约定</b>：{@code material} 由调用方持有，本方法<b>不</b>擦除它，
     * 调用方使用完毕后必须自行 {@code Arrays.fill(material, (byte) 0)}。
     */
    static byte[] deriveSm4Key(byte[] material) {
        org.bouncycastle.crypto.digests.SM3Digest digest = new org.bouncycastle.crypto.digests.SM3Digest();
        digest.update(material, 0, material.length);
        byte[] hash = new byte[digest.getDigestSize()];
        digest.doFinal(hash, 0);
        byte[] key = Arrays.copyOf(hash, 16);
        Arrays.fill(hash, (byte) 0);
        return key;
    }

    /** 返回密钥副本，调用方用完应立即擦除；返回副本避免外部篡改常驻密钥 */
    public byte[] getKeyCopy() {
        byte[] key = this.sm4Key;
        if (key == null) {
            throw new SmCryptoException("SM4 未启用或密钥未加载");
        }
        return Arrays.copyOf(key, key.length);
    }

    public boolean isLoaded() {
        return sm4Key != null;
    }

    /** Base64 可能是“密钥材料本身就是 Base64 字符串”，此处不做解码，统一按 UTF-8 原字节做 SM3 派生 */
    @SuppressWarnings("unused")
    private static byte[] tryBase64Decode(String s) {
        try {
            return Base64.getDecoder().decode(s.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public void destroy() {
        byte[] key = this.sm4Key;
        if (key != null) {
            Arrays.fill(key, (byte) 0);
            this.sm4Key = null;
        }
    }
}
