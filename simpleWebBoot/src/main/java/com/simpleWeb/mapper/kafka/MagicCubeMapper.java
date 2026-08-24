package com.simpleWeb.mapper.kafka;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simpleWeb.entity.db.kafka.MagicCubeDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 魔方Mapper
 */
@Mapper
public interface MagicCubeMapper extends BaseMapper<MagicCubeDO> {
    
    /**
     * 根据魔方名称查询
     */
    MagicCubeDO selectByMagicName(@Param("magicName") String magicName);
}
