package org.basic;

import groovy.util.logging.Slf4j;
import net.bytebuddy.description.type.TypeDescription;
import org.basic.entity.Orders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.stream.Stream;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/8/20 20:16
 * @Description:
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DemoRunner.class)
@Slf4j
public class RedisTest {

    // 使用自己配置的redisTemplate
    @Resource
    private RedisTemplate<String, Orders> redisMyTemplate;

    @Test
    public void basicRedisTest() {
        Orders orders = new Orders();
        orders.setId(1);
        orders.setAmount(1.1);
        orders.setOrderType(1);
        orders.setCustomerId(2);
        redisMyTemplate.opsForValue().set("or",orders);
    }

}
