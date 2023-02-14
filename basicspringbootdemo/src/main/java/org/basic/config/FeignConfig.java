package org.basic.config;

import feign.Client;
import feign.Feign;
import feign.okhttp.OkHttpClient;
import lombok.Getter;
import lombok.Setter;
import org.basic.interceptor.OkHttpInterceptor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.commons.httpclient.OkHttpClientConnectionPoolFactory;
import org.springframework.cloud.commons.httpclient.OkHttpClientFactory;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/12/19 23:50
 * @Description:
 */
@Configuration
@ConditionalOnClass(Feign.class)
@AutoConfigureAfter(FeignAutoConfiguration.class)
public class FeignConfig {

    @Getter
    @Setter
    @Configuration
    @ConfigurationProperties(prefix = "feign.okhttp")
    @ConditionalOnProperty(name = "feign.okhttp.enabled", havingValue = "true")
    protected static class OkHttpProperties {
        boolean followRedirects = true;
        int connectTimeout = 5000;
        boolean disableSslValidation = false;
        int readTimeout = 5000;
        int writeTimeout = 5000;
        // 是否自动重连
        boolean retryOnConnectionFailure = true;
        // 最大空闲连接
        int maxIdleConnections = 10;
        // 默认保持5分钟
        long keepAliveDuration = 1000 * 60 * 5L;
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({OkHttpClient.class})
    @ConditionalOnMissingBean({okhttp3.OkHttpClient.class})
    @ConditionalOnProperty({"feign.okhttp.enabled"})
    protected static class OkHttpFeignConfiguration {

        private okhttp3.OkHttpClient okHttpClient;

        protected OkHttpFeignConfiguration() {

        }

        @Bean
        @ConditionalOnMissingBean(Client.class)
        public Client feignClient(okhttp3.OkHttpClient client) {
            return new OkHttpClient(client);
        }

        @Bean
        public okhttp3.OkHttpClient client(OkHttpClientFactory httpClientFactory, OkHttpProperties properties, OkHttpClientConnectionPoolFactory connectionPoolFactory) {
            this.okHttpClient = httpClientFactory.createBuilder(properties.isDisableSslValidation())
                    .connectTimeout(properties.getConnectTimeout(), TimeUnit.MILLISECONDS)
                    .followRedirects(properties.isFollowRedirects())
                    .readTimeout(properties.getReadTimeout(),TimeUnit.MILLISECONDS)
                    .writeTimeout(properties.getWriteTimeout(),TimeUnit.MILLISECONDS)
                    .retryOnConnectionFailure(properties.isRetryOnConnectionFailure())
                    .connectionPool(connectionPoolFactory.create(properties.getMaxIdleConnections(),properties.getKeepAliveDuration(),TimeUnit.MILLISECONDS))
                    .addInterceptor(new OkHttpInterceptor())
                    .build();
            return this.okHttpClient;
        }

        @PreDestroy
        public void destory() {
            if (this.okHttpClient != null) {
                this.okHttpClient.dispatcher().executorService().shutdown();
                this.okHttpClient.connectionPool().evictAll();
            }
        }
    }

}
