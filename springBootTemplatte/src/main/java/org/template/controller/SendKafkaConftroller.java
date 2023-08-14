package org.template.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**********************************
 * @author zhang zhao lin
 * @date 2023年01月23日 10:41
 * @Description:
 **********************************/
@RestController
@RequestMapping("/v1")
public class SendKafkaConftroller {

    @Resource
    private KafkaTemplate<String,String> kafkaTemplate;

    @RequestMapping(value = "/send")
    public String send(@RequestParam String message) {
        System.out.println("kafka send message " + message);
        kafkaTemplate.send("mytest1",message);
        return message;
    }


}
