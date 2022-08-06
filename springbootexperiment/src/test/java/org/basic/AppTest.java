package org.basic;

import groovy.util.logging.Slf4j;
import org.apache.shardingsphere.api.hint.HintManager;
import org.apache.shardingsphere.core.strategy.keygen.SnowflakeShardingKeyGenerator;
import org.basic.entity.FileInfo;
import org.basic.entity.Orders;
import org.basic.entity.T_Dic;
import org.basic.entity.User;
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
public class AppTest {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 测试inline 分片策略
     */
    @Test
    public void testDB0_inline_insert() {
        for (int i = 0; i < 10; i++) {
            Orders orders = new Orders();
            orders.setId(i);
            orders.setOrderType(i);
            orders.setCustomerId(i);
            orders.setAmount(1000.0 * i);
            orderMapper.insert(orders);

            Orders orders1 = orderMapper.selectOneById(1);
            System.out.println(orders1);
        }
    }

    /**
     * 测试standard 分片策略
     */
    @Test
    public void testDB0_standard_selectRange() {
        List<Orders> orders = orderMapper.selectOrderByRange();
        orders.forEach(System.out::println);

        List<Orders> orders1 = orderMapper.selectOrderByRangeBetween();
        orders1.forEach(System.out::println);
    }

    /**
     * 测试 复合complex 分片策略
     */
    @Test
    public void testDB0_complexRange() {
        /*long id = 1;
        for (int storageType = 0; storageType <= 1; storageType++) {
            for (; id <= storageType * 2 + 2; id++) {
                FileInfo fields = new FileInfo();
                fields.setId(id);
                fields.setStorageType(storageType);
                fields.setName("ShardingJDBC 成神之路");
                orderMapper.insertFileTb(fields);
            }
        }*/
        /*List<FileInfo> fileInfo = orderMapper.getFileInfo();
        System.out.println(fileInfo);*/

        List<FileInfo> fileInfo = orderMapper.getRangeFileInfo();
        System.out.println(fileInfo);
    }

    /**
     * 测试hint 分片策略
     */
    @Test
    public void testDB0_hint_select() {
        try (HintManager hintManager = HintManager.getInstance()) {
            // 从两个表中获取数据，第二个参数是索引
            hintManager.addTableShardingValue("orders", 0);
            hintManager.addTableShardingValue("orders", 1);
            Orders orders = orderMapper.selectOneById(2);
            System.out.println(orders);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBroadcastable() {
        int c = 0;
        SnowflakeShardingKeyGenerator generator = new SnowflakeShardingKeyGenerator();
        for (int i = 1; i < 3; i++) {
            T_Dic t_dic = new T_Dic();
            t_dic.setId(generator.generateKey().toString());
            t_dic.setCode("gender");
            t_dic.setV(String.valueOf(i));
            t_dic.setK(i == 1 ? "男" : "女");
            c += orderMapper.insertDefaultDict(t_dic);
        }
        System.out.printf("计数：%d", c);
    }

    /**
     * 测试关联表
     */
    @Test
    public void testAssociationTOrder() {
        System.out.println(orderMapper.getTordersWithAssociation());
    }

    /**
     * 读写分离
     */
    @Test
    public void readAndWrite_part1() {
        User user = new User();
        user.setId(3L);
        user.setName("张三");
        orderMapper.insertUser(user);
        System.out.println(orderMapper.getUserById(3L));
    }
}
