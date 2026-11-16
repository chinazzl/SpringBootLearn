package org.template.crypto;

/**
 * 国密加解密统一异常，解密失败、密钥缺失、文件权限不满足时抛出。
 * 上层启动失败即拒绝建连，满足监管“缺密钥拒绝启动、篡改拒绝解密”。
 */
public class SmCryptoException extends RuntimeException {
    public SmCryptoException(String message) {
        super(message);
    }

    public SmCryptoException(String message, Throwable cause) {
        super(message, cause);
    }
}
