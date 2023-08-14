package com.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**********************************
 * @author zhang zhao lin
 * @date 2022年09月03日 23:43
 * @Description:
 **********************************/
public class Consumer_ExcutorService {

    private static final String TOPIC = "myTopicCluster";
    private static final KafkaConsumer<String, String> KAFKA_CONSUMER;
    private static final Properties PROPERTIES;
    private static ExecutorService executor;

    static {
        PROPERTIES = new Properties();
        PROPERTIES.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        PROPERTIES.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.43.201:9092,192.168.43.201:9093,192.168.43.201:9094");
        PROPERTIES.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        PROPERTIES.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // 自动提交
        PROPERTIES.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        // 设置kafka消费者组的Coordinator收到consumer实例的心跳间隔
        PROPERTIES.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10_000);
        // 设置consumer实例向Coordinator发送心跳的频率
        PROPERTIES.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 500);
        KAFKA_CONSUMER = new KafkaConsumer<String, String>(PROPERTIES);
        KAFKA_CONSUMER.subscribe(Arrays.asList(TOPIC));
    }

    public void execute() {
        executor = Executors.newFixedThreadPool(6);
        while (true) {
            ConsumerRecords<String, String> records = KAFKA_CONSUMER.poll(Duration.ofMillis(5_000));
            if (records != null) {
                executor.submit(new Consumer_Thread(records));
            }

        }
    }

    public void shutdown() {
        try {
            KAFKA_CONSUMER.close();
            if (executor != null) {
                executor.shutdown();
            }
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                System.out.println("time out");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
