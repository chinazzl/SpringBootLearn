package com.springboot.CRUD.service.impl;

import com.springboot.CRUD.dao.IUseJPADao;
import com.springboot.CRUD.entity.Student;
import com.springboot.CRUD.service.IQueryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class QueryServiceImpl implements IQueryService {
    @Resource
    private IUseJPADao jpaDao;

    public List<Student> getAll() {
        List<Student> students = jpaDao.querAll();
        return students;
    }
}
