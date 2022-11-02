package org.template;

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

    @Test
    public void testInsert() {
        List<Map<String,String>> users = new ArrayList<Map<String,String>>();
        Map<String,String> userMap;
        for (int i = 0; i < 6000; i++) {
            userMap = new HashMap<String,String>();
            userMap.put("username","name"+i);
            userMap.put("password","name"+i);
            users.add(userMap);
        }
        userMapper.batchUser(users);
    }
}
