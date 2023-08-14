package org.template.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.template.config.annotation.SpecificAlter;
import org.template.config.wrapper.SpecialWrapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**********************************
 * @author zhang zhao lin
 * @date 2023年08月10日 10:52
 * @Description:
 **********************************/
@Component
public class SpringBootTemplateInitFactory implements ApplicationContextAware, InitializingBean {

    private static ApplicationContext APP_CONTEXT ;
    private static final Map<String,SpecialWrapper> specialWrappers = new ConcurrentHashMap<>();
    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, Object> beansWithAnnotation = APP_CONTEXT.getBeansWithAnnotation(SpecificAlter.class);
        beansWithAnnotation.forEach((beanName,beanInst) -> {
            SpecificAlter annotationOnBean = APP_CONTEXT.findAnnotationOnBean(beanName, SpecificAlter.class);
            if (beanInst instanceof SpecialWrapper) {
                String name = annotationOnBean.name();
                specialWrappers.put(name, (SpecialWrapper) beanInst);
            }
        });
        System.out.format("初始化 beansWithAnnotation => %s", beansWithAnnotation);
        System.out.format("初始化 specialWrappers => %s", specialWrappers);

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        APP_CONTEXT = applicationContext;
    }
}
