package org.template.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.template.dto.QueryBO;
import org.template.dto.QueryrResultDto;
import org.template.dto.TagFilter;

import java.util.List;

/**********************************
 * @author zhang zhao lin
 * @date 2023年08月14日 15:02
 * @Description:
 **********************************/
@Component
@Slf4j
public class BasicQueryImpl implements BasicQuery{

    @Override
    public QueryrResultDto basicQueryResult(QueryBO queryBO) {
        TagFilter tagFilter = queryBO.getTagFilters();
        // 将过滤条件进行分组
        List<String> tagKeyCombine = tagFilter.tagKeyCombination();
        // 获取所有的条件信息
        List<TagFilter> filterAll = tagFilter.toAll();
    /**
     * select main_id, group_concat(tag_key) as tags from tag.tag_relation
     * where (tag_key = 'tag1' and tag_value = 'value1') or (tag_key = 'tag1' and tag_value = 'value12')
     * group by main_id
     * having tags in ( 'tag1,tag1' )  limit 0, 10;
     */

        return null;
    }
}
