package org.template.service.impl;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.template.mapper.UserMapper;
import org.template.pojo.User;
import org.template.service.IUserService;

import javax.annotation.Resource;

/**
 * @author: zhang zhao lin
 * @Date: Create in 2023/7/8 下午7:18
 * @Modified By：
 * @Description:
 */
@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private DataSourceTransactionManager dataSourceTransactionManager;

    @Resource
    private TransactionDefinition transactionDefinition;

    @Override
    public void userHandler() {

        for (int i = 0; i < 10; i++) {
            if (i == 5) {
                User build = User.builder().id(i).userName("add").passWord("55555").build();
               subHandler(build,i);
            } else {
                User build = User.builder().id(i).userName("add" + i).passWord(String.valueOf(i)).build();
                addUser(build);
            }
        }
    }

    private void subHandler(User build,Integer id) {
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        try {
            userMapper.insert(build);
            int i = 1/0;
            userMapper.delete(id);
            dataSourceTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            dataSourceTransactionManager.rollback(transactionStatus);
            throw new RuntimeException(e);
        }finally {
        }
    }

    public void addUser(User user) {
        userMapper.insert(user);
    }

    public void deleteUser(Integer id) {
        userMapper.delete(id);
    }

}
