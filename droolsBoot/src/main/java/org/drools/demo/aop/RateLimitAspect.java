package org.drools.demo.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.drools.demo.Utils.IpUtils;
import org.drools.demo.annotation.RateLimiter;
import org.drools.demo.enums.LimitType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * @author Julyan
 * @version V1.0
 * @Description: 限流切面
 * @Date: 2022/5/18 21:58
 */
@Aspect
@Component
public class RateLimitAspect {

    private static final Logger log = LoggerFactory.getLogger(RateLimitAspect.class);

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private RedisScript<Long> redisScript;

    @Before("@annotation(rateLimiter)")
    public void doBefore(JoinPoint joinPoint, RateLimiter rateLimiter) {
        String key = rateLimiter.key();
        int time = rateLimiter.time();
        int count = rateLimiter.count();
        String combineKey = getCombineKey(rateLimiter, joinPoint);
        List<Object> keys = Collections.singletonList(combineKey);
        try {
            Long number = redisTemplate.execute(redisScript, keys, count, time);
            if (number == null || number.intValue() > count) {
                throw new RuntimeException("访问过于频繁，请稍后再试。");
            }
            log.info("限制请求'{}'，当前请求'{}'，缓存key'{}'", count, number.intValue(), key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("服务器限流异常，请稍后再试。");
        }
    }

    public String getCombineKey(RateLimiter rateLimiter, JoinPoint joinPoint) {
        StringBuffer stringBuffer = new StringBuffer(rateLimiter.key());
        if (rateLimiter.limitType() == LimitType.IP) {
            stringBuffer.append(IpUtils.getIpAddress(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getRequest())).append("-");
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = method.getDeclaringClass();
        stringBuffer.append(targetClass.getName()).append("-").append(method.getName());
        return stringBuffer.toString();
    }
}
