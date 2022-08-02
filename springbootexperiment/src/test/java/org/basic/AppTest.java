package org.basic;

import groovy.util.logging.Slf4j;
import org.basic.entity.Orders;
import org.basic.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

/**
 * Unit test for simple App.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = App.class)
@Slf4j
public class AppTest {

    @Autowired
    private OrderMapper orderMapper;

    @Test
    public void testDB0_insert() {
        for (int i = 0; i < 10; i++) {
            Orders orders = new Orders();
            orders.setId(i);
            orders.setOrderType(i);
            orders.setCustomerId(i);
            orders.setAmount(1000.0 * i);
            orderMapper.insert(orders);
        }
    }

    @Test
    public void testDB0_select() {
        Orders orders = orderMapper.selectOneById(1);
        System.out.println(orders);
    }

    @Test
    public void testDB0_selectRange() {
        List<Orders> orders = orderMapper.selectOrderByRange();
        orders.forEach(System.out::println);

        List<Orders> orders1 = orderMapper.selectOrderByRangeBetween();
        orders1.forEach(System.out::println);
    }
}
