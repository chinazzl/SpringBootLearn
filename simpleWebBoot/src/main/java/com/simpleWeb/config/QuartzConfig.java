package com.simpleWeb.config;

import org.quartz.SchedulerContext;
import org.quartz.SchedulerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Properties;

/**
 * @author: zhaolin
 * @Date: 2025/7/10
 * @Description:
 **/
@Configuration
public class QuartzConfig {

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(ApplicationContext applicationContext) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setOverwriteExistingJobs(true);
        factory.setStartupDelay(10);
        factory.setQuartzProperties(quartzProperties());
        AutowiringSpringBeanJobFactory beanFactory = new AutowiringSpringBeanJobFactory(applicationContext);
        factory.setJobFactory(beanFactory);
        return factory;
    }

    private Properties quartzProperties() {
        Properties props = new Properties();
        props.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
        props.setProperty("org.quartz.scheduler.instanceName", "SimpleWebBootScheduler");
        return props;
    }
}
