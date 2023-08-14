package org.template.config.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**********************************
 * @author zhang zhao lin
 * @date 2023年01月19日 19:29
 * @Description:
 **********************************/
//@Configuration
public class KafkaProducerConfig {
    //@Value("${kafka.bootstrap-server}")
    private String kafkaBootStrapServer;

    private Map<String, Object> producerProperties() {
       Map<String,Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootStrapServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, org.apache.kafka.clients.producer.RoundRobinPartitioner.class);
        return props;
    }

    @Bean
    public DefaultKafkaProducerFactory<String,String> kafkaProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerProperties());
    }

    @Bean
    public KafkaTemplate<String,String> kafkaTemplate() {
        return new KafkaTemplate<>(kafkaProducerFactory());
    }

}
