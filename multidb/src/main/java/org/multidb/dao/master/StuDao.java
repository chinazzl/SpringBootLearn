package org.multidb.dao.master;

import org.multidb.entity.Student;

/**
 * Created by Felix on 2017/7/12.
 */
public interface StuDao {

    Student findStuById(int id);
}
