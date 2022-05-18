package org.drools.demo.annotation;

import org.drools.demo.enums.LimitType;

import java.lang.annotation.*;

/**
 * @author Julyan
 * @version V1.0
 * @Description: 限流注解
 * @Date: 2022/5/18 21:26
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    /**
     * 限流key
     */
    String key() default "redis_limit";

    /**
     * 限流时间 单位秒
     */
    int time() default 60;

    /**
     * 限流次数
     */
    int count() default 100;

    /**
     * 限流类型
     */
    LimitType limitType() default LimitType.DEFAULT;
}
