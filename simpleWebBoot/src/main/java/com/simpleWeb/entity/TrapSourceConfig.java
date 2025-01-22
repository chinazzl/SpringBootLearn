package com.simpleWeb.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: zhaolin
 * @Date: 2025/1/22
 * @Description:
 **/
@Component
@Data
@ConfigurationProperties(prefix = "snmp.trap")
public class TrapSourceConfig {
    private List<TrapSource> sources;
}
