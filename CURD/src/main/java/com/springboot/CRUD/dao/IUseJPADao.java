package com.springboot.CRUD.dao;

import com.springboot.CRUD.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IUseJPADao extends JpaRepository<Student,Integer> {

    @Query(value = "select id as userid, name as username, password as userpassw, age as userage from student",nativeQuery = true)
    List<Student> querAll();
}
