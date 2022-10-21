package org.basic.config.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/10/21 17:38
 * @Description:
 */
@Configuration
public class SpringContextUtils<T> implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Object getBean(String beanName) {
        return StringUtils.isNotBlank(beanName) ? applicationContext.getBean(beanName) : null;
    }

    public Object getBean(Class<T> className) {
        return className != null ? applicationContext.getBean(className) : null;
    }

    public Object getBean(Class<T> className,Object... objects) {
        return className != null ? applicationContext.getBean(className,objects) : null;
    }
}
