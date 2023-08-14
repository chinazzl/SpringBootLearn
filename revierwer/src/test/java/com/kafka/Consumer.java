package com.kafka;

import java.util.concurrent.TimeUnit;

/**********************************
 * @author zhang zhao lin
 * @date 2022年09月15日 22:36
 * @Description: kafka 消费者
 **********************************/
public class Consumer {
    public static void main(String[] args) {
        Consumer_ExcutorService excutorService = new Consumer_ExcutorService();
        try {
            excutorService.execute();
            sleep(2);
        }finally {
            excutorService.shutdown();
        }
    }

    private static void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
