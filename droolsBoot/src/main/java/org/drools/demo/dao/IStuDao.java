package org.drools.demo.dao;

import org.drools.demo.Entity.vo.StudentVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Julyan
 * @version V1.0
 * @Description:
 * @Date: 2022/4/14 15:10
 */
@Repository
public interface IStuDao extends JpaRepository<StudentVO,Integer> {

    List<StudentVO> findAll();
}
