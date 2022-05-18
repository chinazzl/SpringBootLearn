package org.drools.demo.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Julyan
 * @version V1.0
 * @Description: 全局异常处理类
 * @Date: 2022/5/18 23:15
 */
@RestControllerAdvice
public class GloableExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Map<String, Object> runException(Exception e) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", 500);
        map.put("message", e.getMessage());
        return map;
    }
}
