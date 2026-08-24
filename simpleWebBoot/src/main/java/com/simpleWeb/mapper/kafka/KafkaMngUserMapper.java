package com.simpleWeb.mapper.kafka;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simpleWeb.entity.db.kafka.KafkaMngUserDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Kafka用户管理Mapper
 */
@Mapper
public interface KafkaMngUserMapper extends BaseMapper<KafkaMngUserDO> {
    
    /**
     * 根据用户名查询
     */
    KafkaMngUserDO selectByUserName(@Param("userName") String userName);
}
