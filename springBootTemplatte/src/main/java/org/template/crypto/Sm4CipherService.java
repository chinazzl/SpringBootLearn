package org.template.crypto;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.springframework.stereotype.Component;

/**
 * SM4-128/GCM/NoPadding 加解密服务（单密钥，无密钥分片）。
 *
 * <p>密文格式：{@code SM4(Base64(IV(12字节) || 密文 || GCM-Tag(16字节)))}，
 * 每次加密使用随机 IV，同一明文两次加密结果不同；篡改后 GCM 标签校验失败抛
 * {@link SmCryptoException}。
 *
 * <p>密钥来源：{@link Sm4KeyManager#getKeyCopy()}（环境变量 {@code SM4_KEY}
 * 或 600 权限密钥文件，经 SM3 派生 16 字节），用完即擦除副本。
 */
@Component
public class Sm4CipherService {

    private static final String PREFIX = "SM4(";
    private static final String SUFFIX = ")";
    private static final int IV_LEN = 12;
    private static final int TAG_BIT_LEN = 128;

    private final Sm4KeyManager keyManager;
    private final SecureRandom random = new SecureRandom();

    public Sm4CipherService(Sm4KeyManager keyManager) {
        if (keyManager == null) {
            throw new SmCryptoException("Sm4KeyManager 未注入，拒绝启动");
        }
        this.keyManager = keyManager;
    }

    public boolean isEncrypted(String value) {
        return value != null && value.startsWith(PREFIX) && value.endsWith(SUFFIX) && value.length() > 5;
    }

    /** 明文加密为 SM4(...)；null 直接返回 null */
    public String encrypt(String plain) {
        if (plain == null) {
            return null;
        }
        byte[] key = keyManager.getKeyCopy();
        byte[] iv = new byte[IV_LEN];
        random.nextBytes(iv);
        byte[] src = plain.getBytes(StandardCharsets.UTF_8);
        try {
            GCMBlockCipher cipher = new GCMBlockCipher(new SM4Engine());
            cipher.init(true, new AEADParameters(new KeyParameter(key), TAG_BIT_LEN, iv));
            byte[] out = new byte[cipher.getOutputSize(src.length)];
            int len = cipher.processBytes(src, 0, src.length, out, 0);
            cipher.doFinal(out, len);
            byte[] ivAndCipher = new byte[IV_LEN + out.length];
            System.arraycopy(iv, 0, ivAndCipher, 0, IV_LEN);
            System.arraycopy(out, 0, ivAndCipher, IV_LEN, out.length);
            Arrays.fill(out, (byte) 0);
            return PREFIX + Base64.getEncoder().encodeToString(ivAndCipher) + SUFFIX;
        } catch (InvalidCipherTextException e) {
            throw new SmCryptoException("SM4 加密失败", e);
        } finally {
            Arrays.fill(key, (byte) 0);
            Arrays.fill(iv, (byte) 0);
            Arrays.fill(src, (byte) 0);
        }
    }

    /**
     * 解密；非 SM4(...) 格式原样返回（兼容本地开发明文，生产禁止明文）。
     * 篡改/密钥错误抛 {@link SmCryptoException}，异常信息不回显明文。
     */
    public String decrypt(String value) {
        if (value == null) {
            return null;
        }
        if (!isEncrypted(value)) {
            return value;
        }
        byte[] plain = decryptToBytes(value);
        try {
            // 经 bytes -> chars -> String，减少 String 明文驻留之外的中间拷贝由调用方擦除
            CharBuffer chars = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(plain));
            char[] arr = new char[chars.remaining()];
            chars.get(arr);
            String result = new String(arr);
            Arrays.fill(arr, '\0');
            return result;
        } finally {
            Arrays.fill(plain, (byte) 0);
        }
    }

    /**
     * 解密为 char[]，调用方用完必须 {@code Arrays.fill(chars, '\0')} 擦除，
     * 满足监管“初始化后将解密变量置空，避免内存攻击”。
     */
    public char[] decryptToChars(String value) {
        if (value == null) {
            return null;
        }
        if (!isEncrypted(value)) {
            return value.toCharArray();
        }
        byte[] plain = decryptToBytes(value);
        try {
            CharBuffer chars = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(plain));
            char[] arr = new char[chars.remaining()];
            chars.get(arr);
            return arr;
        } finally {
            Arrays.fill(plain, (byte) 0);
        }
    }

    private byte[] decryptToBytes(String value) {
        String body = value.substring(PREFIX.length(), value.length() - SUFFIX.length());
        byte[] key = keyManager.getKeyCopy();
        byte[] ivAndCipher;
        try {
            ivAndCipher = Base64.getDecoder().decode(body);
        } catch (IllegalArgumentException e) {
            Arrays.fill(key, (byte) 0);
            throw new SmCryptoException("SM4 密文 Base64 非法，解密失败");
        }
        try {
            if (ivAndCipher.length <= IV_LEN) {
                throw new SmCryptoException("SM4 密文长度非法，解密失败");
            }
            byte[] iv = Arrays.copyOfRange(ivAndCipher, 0, IV_LEN);
            byte[] cipherBytes = Arrays.copyOfRange(ivAndCipher, IV_LEN, ivAndCipher.length);
            try {
                GCMBlockCipher cipher = new GCMBlockCipher(new SM4Engine());
                cipher.init(false, new AEADParameters(new KeyParameter(key), TAG_BIT_LEN, iv));
                byte[] out = new byte[cipher.getOutputSize(cipherBytes.length)];
                int len = cipher.processBytes(cipherBytes, 0, cipherBytes.length, out, 0);
                cipher.doFinal(out, len);
                return out;
            } catch (InvalidCipherTextException e) {
                throw new SmCryptoException("SM4 解密失败：密文被篡改或密钥不正确", e);
            } finally {
                Arrays.fill(iv, (byte) 0);
                Arrays.fill(cipherBytes, (byte) 0);
            }
        } finally {
            Arrays.fill(key, (byte) 0);
            Arrays.fill(ivAndCipher, (byte) 0);
        }
    }
}
