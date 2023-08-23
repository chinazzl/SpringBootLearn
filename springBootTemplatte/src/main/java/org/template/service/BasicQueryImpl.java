package org.template.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.db.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.template.dto.QueryBO;
import org.template.dto.ResultResponse;
import org.template.dto.TCmdbInfo;
import org.template.dto.TagFilter;
import org.template.mapper.TcmdbQueryMapper;
import org.template.service.impl.BaseService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**********************************
 * @author zhang zhao lin
 * @date 2023年08月14日 15:02
 * @Description:
 **********************************/
@Component
@Slf4j
public class BasicQueryImpl extends BaseService implements BasicQuery {
    @Resource
    private TcmdbQueryMapper tcmdbQueryMapper;

    @Override
    public ResultResponse basicQueryResult(QueryBO queryBO) {
        Pair<Boolean, String> validate = validate(queryBO, QueryBO.Query.class);
        if (!validate.getKey()) {
            throw new RuntimeException("Invalid query Parameters. exception: " + validate.getValue());
        }
        ResultResponse response = new ResultResponse();
        List<ResultResponse.Details> details = new ArrayList<>();
        List<String> queryTags = queryBO.getQueryTags();
        TagFilter tagFilter = queryBO.getTagFilters();
        // 将过滤条件进行分组
        List<String> tagKeyCombine = Optional.ofNullable(tagFilter).map(TagFilter::tagKeyCombination).orElse(null);
        // 获取所有的条件信息
        List<TagFilter> filterAll = Optional.ofNullable(tagFilter).map(TagFilter::toAll).orElse(null);
        int page = queryBO.getPage();
        int pageSize = queryBO.getPageSize();
        Page pageEntity = Page.of(page, pageSize);
        /**
         * select TARGET, listagg(tag_key, ',') within group(order by tag_key) as tags
         *   from T_CMDB_INFO
         *  where (tag_key = 'UNDER_MAGIC' and TAG_VALUE = 'OS;WAS')
         *     or (tag_key = 'SYSTEM_NAME' and TAG_VALUE = 'xx系统')
         *  group by TARGET;
         *  获取所有的满足条件的ip
         */
        List<String> targetAfterFilter = CollectionUtil.isEmpty(queryBO.getTargets()) ? tcmdbQueryMapper.getCmdbAfterFilter(filterAll, tagKeyCombine, pageEntity) : queryBO.getTargets();
        // 获取每个元素的值类型
        QueryWrapper<TCmdbInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(TCmdbInfo::getTargetKey, TCmdbInfo::getValueType)
                .eq(TCmdbInfo::getTarget, targetAfterFilter.stream().findFirst().orElseThrow(() -> new RuntimeException("No target")));
        List<TCmdbInfo> tCmdbInfos = tcmdbQueryMapper.selectList(queryWrapper);
        Map<String, List<String>> cmdbGroupByValueType = tCmdbInfos.stream()
                .collect(Collectors.groupingBy(TCmdbInfo::getValueType, Collectors.mapping(cmdb -> {
                    if (queryTags.contains(cmdb.getTargetKey())) {
                        return cmdb.getTargetKey();
                    }
                    return null;
                }, Collectors.toList())));
        log.info("targets => {}", targetAfterFilter);
        // 再根据获取的target获取要查询的一些数据
        List<TCmdbInfo> total = new ArrayList<>();
        cmdbGroupByValueType.forEach((k, v) -> {
            List<TCmdbInfo> cmdbInfo = tcmdbQueryMapper.getCmdbInfo(v, targetAfterFilter, k);
            total.addAll(cmdbInfo);
        });
        log.info("{}", total);

        Map<String, List<ResultResponse.Tag>> collect = total.stream().collect(Collectors.groupingBy(TCmdbInfo::getTarget, Collectors.mapping(t -> {
            ResultResponse.Tag tag = new ResultResponse().new Tag();
            BeanUtils.copyProperties(t, tag);
            return tag;
        }, Collectors.toList())));

        collect.forEach((k, v) -> {
            ResultResponse.Details detail = new ResultResponse().new Details();
            detail.setTarget(k);
            detail.setTags(v);
            details.add(detail);
        });
        response.setListData(details);
        return response;
    }

}
