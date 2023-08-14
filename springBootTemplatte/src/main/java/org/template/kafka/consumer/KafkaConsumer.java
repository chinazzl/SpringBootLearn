package org.template.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**********************************
 * @author zhang zhao lin
 * @date 2023年01月20日 14:12
 * @Description:
 **********************************/
@Component
public class KafkaConsumer {

    @KafkaListener(topics = "mytest",containerFactory = "listenerContainerFactory",errorHandler = "myTestErrorHandler")
    public void arriveMessage(ConsumerRecords<String,String> record) {
        try {
            throw new IllegalStateException("非法异常");
        }catch (Exception e) {
            System.out.println("处理kafka");
            throw new RuntimeException(e);
        }
    }
}
