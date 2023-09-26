package org.template.config.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.template.config.annotation.AddAuthorizationHeader;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**********************************
 * @author zhang zhao lin
 * @date 2023年09月18日 10:55
 * @Description: 一个通用的
 **********************************/
@Aspect
@Component
@Slf4j
public class AuthorizationAspect {

    private final ApplicationContext applicationContext;

    public AuthorizationAspect(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Before("feignClient()")
    public void addHeader(JoinPoint joinPoint) {
        Class<?> clazz = joinPoint.getSignature().getDeclaringType();
        Method method;
        try {
            method = clazz.getMethod(joinPoint.getSignature().getName(), Arrays.stream(joinPoint.getArgs()).map(Object::getClass).toArray(Class[]::new));
        }catch (Exception e) {
            log.error("Exception");
            e.printStackTrace();
            return;
        }
        AddAuthorizationHeader addAuthorizationHeader = method.getAnnotation(AddAuthorizationHeader.class);
        if (Objects.isNull(addAuthorizationHeader)) {
            return;
        }
        IAuthorizationStrategy services = null;
        try {
            services = applicationContext.getBean(addAuthorizationHeader.name(), IAuthorizationStrategy.class);
        }catch (Exception e) {
            log.error("Exception");
        }
        if (services == null) {
            return;
        }
        try {
            String token = services.token();
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof HttpHeaders) {
                    HttpHeaders headers = (HttpHeaders) arg;
                    headers.add(HttpHeaders.AUTHORIZATION, token);
                }
            }
        } catch (Exception e) {

        }

    }

}
