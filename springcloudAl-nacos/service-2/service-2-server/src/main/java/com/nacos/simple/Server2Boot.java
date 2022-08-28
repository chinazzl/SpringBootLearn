package com.nacos.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**********************************
 * @author zhang zhao lin
 * @date 2022年08月28日 22:16
 * @Description:
 **********************************/
@SpringBootApplication
@EnableDiscoveryClient
public class Server2Boot {

    public static void main(String[] args) {
        SpringApplication.run(Server2Boot.class, args);
    }
}
