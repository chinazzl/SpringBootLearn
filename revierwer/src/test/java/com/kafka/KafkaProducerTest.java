package com.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**********************************
 * @author zhang zhao lin
 * @date 2022年09月03日 23:24
 * @Description:
 **********************************/

public class KafkaProducerTest {

    private static final KafkaProducer<String, String> kafkaProducer;
    private static final Properties props;

    static {
        props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.43.201:9092,192.168.43.201:9093,192.168.43.201:9094");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, org.apache.kafka.clients.producer.RoundRobinPartitioner.class);
        kafkaProducer = new KafkaProducer<String, String>(props);
    }

    /**
     * Creates a record with a specified timestamp to be sent to a specified topic and partition
     * topic The record will be appended to
     * partition The partition to which the record should be sent
     * timestamp The timestamp of the record, in milliseconds since epoch. If null, the producer will assign the
     * timestamp using System.currentTimeMillis().
     * key       The key that will be included in the record
     * value     The record contents
     */
    public static void main(String[] args) {
        try {
            IntStream.rangeClosed(0, 2).forEach(i -> {
                Thread thread = new Thread(() -> {
                    kafkaProducer.send(new ProducerRecord<String, String>("myTopicCluster", "hello", "world"),
                            (result, callback) -> {
                                System.out.println(result + " == " + Optional.ofNullable(callback).map(Throwable::getMessage).orElse("没有异常"));
                            });
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            kafkaProducer.close();
        }

    }
}
