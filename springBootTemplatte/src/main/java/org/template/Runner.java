package org.template;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;

/**********************************
 * @author zhang zhao lin
 * @date 2022年11月02日 22:03
 * @Description:
 **********************************/
@SpringBootApplication
@MapperScan("org.template.mapper")
public class Runner {
    public static void main(String[] args) {
        SpringApplication.run(Runner.class,args);
    }
}
