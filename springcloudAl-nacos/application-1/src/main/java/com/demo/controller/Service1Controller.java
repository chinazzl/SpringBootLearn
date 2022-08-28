package com.demo.controller;

import com.nacos.api.ConsumerService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**********************************
 * @author zhang zhao lin
 * @date 2022年08月28日 13:56
 * @Description:
 **********************************/
@RestController
public class Service1Controller {

    @Reference
    private ConsumerService consumerService;

    @GetMapping("/service1")
    public String getService1() {
        String result = consumerService.consumerserver1();
        return "service1 result is " + result;
    }
}
