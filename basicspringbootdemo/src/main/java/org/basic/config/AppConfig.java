package org.basic.config;

import cn.hutool.crypto.symmetric.AES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/12/4 20:49
 * @Description:
 */
@Configuration
public class AppConfig {

    @Autowired
    private CryptoConfig cryptoConfig;

    @Bean
    public AES aes() {
        return new AES(cryptoConfig.getMode(), cryptoConfig.getPadding(), cryptoConfig.getKey().getBytes(),cryptoConfig.getIv().getBytes(StandardCharsets.UTF_8));
    }
}
