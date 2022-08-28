package com.nacos.consumer.controller;

import com.nacos.consumer.remote.ProviderRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**********************************
 * @author zhang zhao lin
 * @date 2022年08月27日 19:45
 * @Description:
 **********************************/
@RestController
public class CosumerController {

    @Autowired
    private ProviderRemote providerRemote;

    @GetMapping("/consumerserver")
    public String getConsumerserver() {
        String nacosServer = providerRemote.getNacosServer();
        return "consumer recieved " + nacosServer;
    }
}
