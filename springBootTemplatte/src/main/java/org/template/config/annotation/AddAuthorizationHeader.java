package org.template.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**********************************
 * @author zhang zhao lin
 * @date 2023年09月26日 14:39
 * @Description: 统一给请求假如header
 **********************************/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AddAuthorizationHeader {

    String name();
}
