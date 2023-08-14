package com.simple.alibaba.config;

import com.simple.alibaba.config.remoteFallback.Server3RemoteFallbackFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**********************************
 * @author zhang zhao lin
 * @date 2022年09月12日 10:49
 * @Description:
 **********************************/
@Configuration
public class RemoteConfig {

    @Bean
    public Server3RemoteFallbackFactory server3RemoteFallbackFactory() {
        return new Server3RemoteFallbackFactory();
    }
}
