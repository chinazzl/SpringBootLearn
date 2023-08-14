package com.server3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**********************************
 * @author zhang zhao lin
 * @date 2022年09月11日 12:24
 * @Description:
 **********************************/
@SpringBootApplication
@EnableDiscoveryClient
public class Server9090 {
    public static void main(String[] args) {
        SpringApplication.run(Server9090.class, args);
    }
}
