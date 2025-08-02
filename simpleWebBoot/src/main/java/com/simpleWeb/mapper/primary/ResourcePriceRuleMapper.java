package com.simpleWeb.mapper.primary;

import com.simpleWeb.entity.db.primary.ResourcePriceDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * @author: zhaolin
 * @Date: 2025/7/26
 * @Description:
 **/
@Mapper
public interface ResourcePriceRuleMapper {

    List<ResourcePriceDO> resourcePriceDOList(@Param("collectDate")LocalDate collectDate);
    List<ResourcePriceDO> resourcePriceDOListByAreaId(@Param("areaId")Long areaId);
}
