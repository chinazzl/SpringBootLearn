package org.basic.utils;

import cn.hutool.crypto.symmetric.AES;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/12/4 18:22
 * @Description:
 */
@Component
public class AESUtils {

    private static AES aes;

    @Resource
    private AES iocAes;

    @PostConstruct
    public void setAes() {
        AESUtils.aes = this.iocAes;
    }

    public static AES getAes() {
        return aes;
    }

    public static String encryptHex(byte[] data) {
        return aes.encryptHex(data);
    }
    public static String encryptHex(InputStream data) {
        return aes.encryptHex(data);
    }

    public static String encryptHex(String data) {
        return aes.encryptHex(data, StandardCharsets.UTF_8);
    }

    /**
     * 解密
     */

    public static String decrypt(byte[] data) {
        return aes.decryptStr(data, StandardCharsets.UTF_8);
    }

    public static String decrypt(InputStream data) {
        return aes.decryptStr(data);
    }

    public static String decrypt(String data) {
        return aes.decryptStr(data, StandardCharsets.UTF_8);
    }
}
