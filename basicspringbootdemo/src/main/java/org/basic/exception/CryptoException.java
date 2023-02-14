package org.basic.exception;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/12/4 17:50
 * @Description:
 */
public class CryptoException extends CustomizeException {
    public CryptoException(String message) {
        super(message);
    }

    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }
}
