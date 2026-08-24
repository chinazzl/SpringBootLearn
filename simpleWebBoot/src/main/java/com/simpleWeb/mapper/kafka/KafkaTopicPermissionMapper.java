package com.simpleWeb.mapper.kafka;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simpleWeb.entity.db.kafka.KafkaTopicPermissionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Kafka主题权限Mapper
 */
@Mapper
public interface KafkaTopicPermissionMapper extends BaseMapper<KafkaTopicPermissionDO> {
    
    /**
     * 根据魔方ID、主题ID、用户ID查询权限
     */
    KafkaTopicPermissionDO selectByMagicTopicUser(
            @Param("magicId") Long magicId, 
            @Param("topicId") Long topicId, 
            @Param("userId") Long userId);
}
