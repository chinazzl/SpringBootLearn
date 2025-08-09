package com.simpleWeb.mapper.primary;

import com.simpleWeb.entity.db.primary.ResourceDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: zhaolin
 * @Date: 2025/7/17
 * @Description:
 **/
@Mapper
public interface ResourceMapper {

    void insert(List<ResourceDO> resourceDOS);

    int update(ResourceDO resourceDO);

    int batchUpdate(List<ResourceDO> resourceDOS);

    int findByServiceType(@Param("serviceType") String serviceType);

    List<ResourceDO> selectResourceDOList();

    List<ResourceDO> queryResourceGroupTime();

}
