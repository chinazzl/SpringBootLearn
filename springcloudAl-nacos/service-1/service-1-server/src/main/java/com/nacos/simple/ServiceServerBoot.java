package com.nacos.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**********************************
 * @author zhang zhao lin
 * @date 2022年08月28日 13:22
 * @Description:
 **********************************/
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceServerBoot {
    public static void main(String[] args) {
        SpringApplication.run(ServiceServerBoot.class, args);
    }
}
