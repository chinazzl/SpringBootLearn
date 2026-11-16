package org.template;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**********************************
 * @author zhang zhao lin
 * @date 2022年11月02日 22:03
 * @Description:
 **********************************/
@SpringBootApplication
@MapperScan("org.template.mapper")
public class TemplateRunner {
    public static void main(String[] args) {
        SpringApplication.run(TemplateRunner.class, args);
    }
}
