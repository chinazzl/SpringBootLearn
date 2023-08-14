package com.simple.alibaba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**********************************
 * @author zhang zhao lin
 * @date 2022年09月09日 23:38
 * @Description:
 **********************************/
@SpringBootApplication
@EnableFeignClients("com.simple.alibaba.remote")
@EnableDiscoveryClient
public class SImpleDemoRunner {
    public static void main(String[] args) {
        SpringApplication.run(SImpleDemoRunner.class, args);
    }
}
