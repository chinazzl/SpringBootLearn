package org.template.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;
import org.template.pojo.User;

import java.util.List;
import java.util.Map;

/**********************************
 * @author zhang zhao lin
 * @date 2022年11月02日 22:08
 * @Description:
 **********************************/
@Mapper
@Component
public interface UserMapper {

    void batchUser(List<Map<String, String>> users);

    /**
     * 使用mybatis的批处理进行修改。
     *
     * @param user
     */
   /* void batchUserByMybatis(@Param("user") Map<String, String> user);

    @Insert("insert into user(uid,name,password) values (#{user.id},#{user.userName}, #{user.passWord})")
    void insert(@Param("user") User user);

    @Update("update user set password where uid = 5")
    void update(@Param("user") User user);

    @Delete("delete from user where uid = #{id}")
    void delete(@Param("id") Integer id);*/
}
