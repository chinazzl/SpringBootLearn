package org.template.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

/**********************************
 * @author zhang zhao lin
 * @date 2023年01月20日 13:51
 * @Description:
 **********************************/
//@Configuration
//@EnableKafka
public class KafkaConsumerConfig {

    //@Value("${kafka.bootstrap-server}")
    private String kafkaBootStrapServer;

    private Map<String, Object> getConsumerProperties() {
        Map<String,Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootStrapServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,5);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,"kafka");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,10);
        return props;
    }

    //@Bean("consumerFactory")
    public DefaultKafkaConsumerFactory<String,String> getConsumerFactory() {
        return new DefaultKafkaConsumerFactory<String,String>(getConsumerProperties());
    }

    //@Bean("listenerContainerFactory")
    //@ConditionalOnMissingBean(name = "listenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> listenerContainerFactory(DefaultKafkaConsumerFactory consumerFactory) {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(3);
        factory.setBatchListener(true);
        return factory;
    }

}
