package org.template.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**********************************
 * @author zhang zhao lin
 * @date 2023年08月10日 10:49
 * @Description:
 **********************************/

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SpecificAlter {

    String name() default "";
}
