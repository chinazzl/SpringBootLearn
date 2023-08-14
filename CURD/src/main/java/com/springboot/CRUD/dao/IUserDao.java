package com.springboot.CRUD.dao;

import com.springboot.CRUD.entity.Student;

import java.util.List;

public interface IUserDao {

    List<Student> findAllUser();
}
