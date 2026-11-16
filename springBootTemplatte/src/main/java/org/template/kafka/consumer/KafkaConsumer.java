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

    /**
     * 容器工厂 bean 名。
     *
     * <p>Spring Boot 4 中 Kafka 自动配置已拆分到 spring-boot-kafka 模块，
     * 自动装配的容器工厂 bean 名为 {@code kafkaListenerContainerFactory}
     * （Boot 2.x/3.x 同名）。原代码写的是 {@code listenerContainerFactory}，
     * 那只是 {@code KafkaConsumerConfig} 里一个被注释掉的自定义 bean 名，
     * 一旦启用自动装配就会找不到，启动失败。
     */
    @KafkaListener(topics = "mytest", containerFactory = "kafkaListenerContainerFactory", errorHandler = "myTestErrorHandler")
    public void arriveMessage(ConsumerRecords<String,String> record) {
        try {
            throw new IllegalStateException("非法异常");
        }catch (Exception e) {
            System.out.println("处理kafka");
            throw new RuntimeException(e);
        }
    }
}
