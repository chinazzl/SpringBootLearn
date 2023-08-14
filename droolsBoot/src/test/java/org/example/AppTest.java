package org.example;

import static org.junit.Assert.assertTrue;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        String json = "{\"event\": {\"name\":\"true\"}}";
        Map<String, String> map = JSONObject.parseObject(json, new TypeReference<Map<String, String>>() {
        });
        System.out.println(map);
    }

    @Test
    public void copy() {
        User user = new User();
        Stu stu = new Stu();
        stu.setName("stu");
        stu.setPassword("password");
        stu.setIsBoy(false);
        BeanUtils.copyProperties(stu, user);
        System.out.println(user);
    }
}
