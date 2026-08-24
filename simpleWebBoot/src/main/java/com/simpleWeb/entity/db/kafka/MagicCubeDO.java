package com.simpleWeb.entity.db.kafka;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 魔方表实体类
 */
@Data
@TableName("MAGIC_CUBE")
public class MagicCubeDO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String magicName;
    
    private Date createTime;
    
    private Date updateTime;
    
    private Integer version;
}
