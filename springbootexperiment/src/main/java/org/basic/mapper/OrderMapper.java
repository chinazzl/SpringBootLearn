package org.basic.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.basic.entity.Orders;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/8/1 16:21
 * @Description:
 */
@Repository
@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO orders(ID,ORDERTYPE,CUSTOMERID,AMOUNT) VALUES (#{id},#{orderType},#{customerId},#{amount})")
    int insert(Orders orders);

    @Select("select * from orders where id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "orderType", column = "orderType"),
            @Result(property = "customerId", column = "customerId"),
            @Result(property = "amount", column = "amount")
    })
    Orders selectOneById(Integer id);

    @Select("select * from orders where id in(1,3)")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "orderType", column = "orderType"),
            @Result(property = "customerId", column = "customerId"),
            @Result(property = "amount", column = "amount")
    })
    List<Orders> selectOrderByRange();

    @Select("select * from orders where id between 1 and 5")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "orderType", column = "orderType"),
            @Result(property = "customerId", column = "customerId"),
            @Result(property = "amount", column = "amount")
    })
    List<Orders> selectOrderByRangeBetween();
}
