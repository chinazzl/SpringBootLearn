package org.basic.config;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/12/4 20:45
 * @Description:
 */
@Configuration
@ConfigurationProperties(prefix = "crypto")
@PropertySource("classpath:crypto.properties")
@Data
@EqualsAndHashCode
public class CryptoConfig {
    private Mode mode;

    private Padding padding;

    private String key;

    private String iv;
}
