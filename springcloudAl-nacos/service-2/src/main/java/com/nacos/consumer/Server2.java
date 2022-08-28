package com.nacos.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**********************************
 * @author zhang zhao lin
 * @date 2022年08月27日 19:42
 * @Description:
 **********************************/

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class Server2 {
    public static void main(String[] args) {
        SpringApplication.run(Server2.class,args);
    }
}
