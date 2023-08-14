package com.springboot.CRUD.service;

import com.springboot.CRUD.entity.Student;

import java.util.List;

public interface IQueryService {

    List<Student> getAll();
}
