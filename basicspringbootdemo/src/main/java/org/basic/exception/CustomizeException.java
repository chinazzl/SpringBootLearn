package org.basic.exception;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/12/4 17:38
 * @Description: 自定义异常
 */
public class CustomizeException extends Exception {

    public CustomizeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomizeException(String message) {
        super(message);
    }

}
