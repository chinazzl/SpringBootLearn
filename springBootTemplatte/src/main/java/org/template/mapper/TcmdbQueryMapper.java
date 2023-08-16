package org.template.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.template.dto.TCmdbInfo;
import org.template.dto.TagFilter;

import java.util.List;
import java.util.Map;

/**********************************
 * @author zhang zhao lin
 * @date 2023年08月15日 8:47
 * @Description:
 **********************************/
@Mapper
public interface TcmdbQueryMapper extends BaseMapper<TCmdbInfo> {

    List<String> getCmdbAfterFilter(@Param("tagFilters") List<TagFilter> filterAll, @Param("tagKeyCombine")List<String> tagKeyCombine);
}
