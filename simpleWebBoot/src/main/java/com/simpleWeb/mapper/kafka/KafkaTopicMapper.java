package com.simpleWeb.mapper.kafka;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simpleWeb.entity.db.kafka.KafkaTopicDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Kafka主题Mapper
 */
@Mapper
public interface KafkaTopicMapper extends BaseMapper<KafkaTopicDO> {
    
    /**
     * 根据主题名称查询
     */
    KafkaTopicDO selectByTopicName(@Param("topicName") String topicName);
}
