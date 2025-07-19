package com.simpleWeb.mapper.cmp;

import com.simpleWeb.entity.db.cmp.CmpResourceDO;
import com.simpleWeb.entity.dto.ResourceDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: zhaolin
 * @Date: 2025/7/17
 * @Description:
 **/
@Mapper
public interface CmpResourceMapper {

    List<CmpResourceDO> queryVmwareResourceDOList();
    List<CmpResourceDO> queryFusionCloudResourceDOList();
}
