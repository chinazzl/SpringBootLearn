package org.basic.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.basic.entity.FileInfo;
import org.basic.entity.Orders;
import org.basic.entity.T_Dic;
import org.basic.entity.T_Order;
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

    /**
     * 测试inline 分片策略
     * @param fields
     * @return
     */
    @Insert("INSERT INTO t_file (id,storage_type,name) values (#{id},#{storageType},#{name})")
    int insertFileTb(FileInfo fields);

    @Select("SELECT * FROM t_file WHERE id = 1 and storage_type = 1")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "storageType", column = "storage_type"),
            @Result(property = "name", column = "name")
    })
    List<FileInfo> getFileInfo();

    /**
     * 测试standard策略和complex策略
     */
    @Select("SELECT * FROM t_file WHERE id between 1 and 5")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "storageType", column = "storage_type"),
            @Result(property = "name", column = "name")
    })
    List<FileInfo> getRangeFileInfo();

    /**
     * 创建广播表
     * @return
     */
    @Insert("insert into t_dict(id,code,k,v) values (#{id},#{code},#{k},#{v})")
    int insertDefaultDict(T_Dic t_dic);

    @Select("SELECT O.ORDER_ID,ITEM.PRICE FROM T_ORDER O INNER JOIN T_ORDER_ITEM ITEM ON O.ORDER_ID = ITEM.ORDER_ID WHERE O.ORDER_ID = 1")
    @Results({
        @Result(property = "orderId",column = "order_id"),
        @Result(property = "price",column = "price")
    })
    List<T_Order> getTordersWithAssociation();

}
