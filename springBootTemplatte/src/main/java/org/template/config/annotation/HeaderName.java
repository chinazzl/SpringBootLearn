package org.template.config.   annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**********************************
 * @author zhang zhao lin
 * @date 2024年09月05日 14:33
 * @Description:
 **********************************/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HeaderName {
    String value() default "";
}
