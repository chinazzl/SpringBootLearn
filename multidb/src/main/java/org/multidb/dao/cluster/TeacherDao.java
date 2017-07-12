package org.multidb.dao.cluster;

import org.multidb.entity.Teacher;

/**
 * Created by Felix on 2017/7/12.
 */
public interface TeacherDao {
    Teacher findTeacherById(int id);
}
