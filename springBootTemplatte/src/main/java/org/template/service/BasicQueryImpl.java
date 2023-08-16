package org.template.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.template.dto.QueryBO;
import org.template.dto.QueryrResultDto;
import org.template.dto.TCmdbInfo;
import org.template.dto.TagFilter;
import org.template.mapper.TcmdbQueryMapper;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**********************************
 * @author zhang zhao lin
 * @date 2023年08月14日 15:02
 * @Description:
 **********************************/
@Component
@Slf4j
public class BasicQueryImpl implements BasicQuery{
@Resource
private TcmdbQueryMapper tcmdbQueryMapper;

    @Override
    public QueryrResultDto basicQueryResult(QueryBO queryBO) {
        TagFilter tagFilter = queryBO.getTagFilters();
        // 将过滤条件进行分组
        List<String> tagKeyCombine = tagFilter.tagKeyCombination();
        // 获取所有的条件信息
        List<TagFilter> filterAll = tagFilter.toAll();
    /**
     * select TARGET, listagg(tag_key, ',') within group(order by tag_key) as tags
     *   from T_CMDB_INFO
     *  where (tag_key = 'UNDER_MAGIC' and TAG_VALUE = 'OS;WAS')
     *     or (tag_key = 'SYSTEM_NAME' and TAG_VALUE = 'xx系统')
     *  group by TARGET;
     */
        List<String> targetAfterFilter = tcmdbQueryMapper.getCmdbAfterFilter(filterAll,tagKeyCombine);
        log.info("targets => {}",targetAfterFilter);
        // 再根据获取的target获取要查询的一些数据
        return null;

    }

}
