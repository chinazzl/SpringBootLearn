package org.template.mapper;

import cn.hutool.db.Page;
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

    List<String> getCmdbAfterFilter(@Param("tagFilters") List<TagFilter> filterAll, @Param("tagKeyCombine")List<String> tagKeyCombine,
                                    @Param("page")Page page);

    /**
     * SELECT TAG_VALUE
     * FROM T_CMDB_INFO WHERE TAG_KEY = 'UNDER_MAGIC'
     * UNION ALL
     * SELECT STD.VALUE_DESC
     * FROM T_CMDB_INFO CI INNER JOIN T_STD_CODE STD
     * ON CI.TARGETID = STD.TAG_ID
     * AND CI.TAG_VALUE = STD.VALUE_CODE
     * WHERE CI.TAG_KEY = 'OS_TYPE'
     * @return
     */
    List<TCmdbInfo> getCmdbInfo(@Param("queryTags") List<String> queryTags, @Param("targets") List<String> targets, @Param("valueType") String valueType);
}
