package com.simpleWeb.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: zhaolin
 * @Date: 2025/8/16
 * @Description:
 **/
@Component
@ConfigurationProperties(prefix = "magic.datasources")
@Data
public class DataSourceProperties {
    private List<DataSourceConfig> configs;

    @Data
    public static class DataSourceConfig {
        private String name;
        private String driverClassName;
        private String url;
        private String username;
        private String password;
    }
}
