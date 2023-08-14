package org.template;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.template.mapper.UserMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**********************************
 * @author zhang zhao lin
 * @date 2022年11月02日 22:06
 * @Description:
 **********************************/
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Runner.class)
public class mybatistest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Test
    public void testInsert() {
        long start = System.currentTimeMillis();
        List<Map<String, String>> users = new ArrayList<Map<String, String>>();
        Map<String, String> userMap;
        for (int i = 0; i < 60000; i++) {
            userMap = new HashMap<String, String>();
            userMap.put("username", "name" + i);
            userMap.put("password", "name" + i);
            users.add(userMap);
        }
        userMapper.batchUser(users);
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - start);
    }

    /**
     * 使用Mybatis的批处理操作
     */
    @Test
    public void testBatchInsert() {
        long start = System.currentTimeMillis();
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);) {
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            Map<String, String> userMap;
            for (int i = 0; i < 60000; i++) {
                userMap = new HashMap<String, String>();
                userMap.put("username", "name" + i);
                userMap.put("password", "name" + i);
                mapper.insertUserByMybatisBatch(userMap);
            }
            long endTime = System.currentTimeMillis();
            System.out.println(endTime - start);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
