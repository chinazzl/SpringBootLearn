package com.springboot.CRUD.dao.userDaoImpl;

import com.springboot.CRUD.dao.IUserDao;
import com.springboot.CRUD.entity.Student;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDaoImpl implements IUserDao {
    public List<Student> findAllUser() {
        return null;
    }
}
