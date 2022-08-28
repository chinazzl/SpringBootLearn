package com.nacos.consumer.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author zhang zhao lin
 * @date 2022年08月27日 19:43
 * @Description
 */
@FeignClient(value = "service1")
public interface ProviderRemote {

    @GetMapping("/nacosServer")
    String getNacosServer();

}
