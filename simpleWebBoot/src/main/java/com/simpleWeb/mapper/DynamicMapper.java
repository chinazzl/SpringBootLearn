package com.simpleWeb.mapper;

import java.util.List;
import java.util.Map;

/**
 * @author: zhaolin
 * @Date: 2025/8/16
 * @Description:
 **/
public interface DynamicMapper {


//    List<Map<String,Object>> selectDynamic(Map<String,Object> map);

    List<Map<String,Object>> selectWithParams(Map<String,Object> map);
}
