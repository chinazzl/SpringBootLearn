package com.simple.alibaba.service.impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.simple.alibaba.service.StudentService;
import org.springframework.stereotype.Service;

/**********************************
 * @author zhang zhao lin
 * @date 2022年09月11日 12:57
 * @Description:
 **********************************/
@Service
public class StudentServiceImpl implements StudentService {

    @Override
    @SentinelResource(value = "student")
    public String getStudentName(String param) {
        System.out.println("获取 student " + param);
        return param;
    }
}
