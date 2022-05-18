package org.drools.demo.enums;

/**
 * @author Julyan
 * @version V1.0
 * @Description: 限流类型
 * @Date: 2022/5/18 21:28
 */
public enum LimitType {
    /**
     * 默认全局限流
     */
    DEFAULT,

    /**
     * 根据IP限流
     */
    IP;
}
