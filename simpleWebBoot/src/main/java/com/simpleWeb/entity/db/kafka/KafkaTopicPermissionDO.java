package com.simpleWeb.entity.db.kafka;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Kafka主题权限表实体类
 */
@Data
@TableName("KAFKA_TOPIC_PERMISSION")
public class KafkaTopicPermissionDO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long magicId;
    
    private Long topicId;
    
    private Long userId;
    
    private String permissionType;
    
    private Date createTime;
    
    private Date updateTime;
    
    private Integer version;
}
