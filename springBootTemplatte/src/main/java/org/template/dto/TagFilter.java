package org.template.dto;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.template.enums.Condition;
import org.template.json.QueryJSON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**********************************
 * @author zhang zhao lin
 * @date 2023年08月14日 14:42
 * @Description:
 **********************************/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagFilter {
    private Condition condition;
    private List<TagFilter> tagFilters;
    private String tagKey;
    private String tagValue;

    private TagFilter(String tagKey, String tagValue, Condition condition) {
        this.condition = condition;
        this.tagKey = tagKey;
        this.tagValue = tagValue;
    }

    private TagFilter(Condition condition, List<TagFilter> tagFilters) {
        this.condition = condition;
        this.tagFilters = tagFilters;
    }

    public static TagFilter equals(String tagKey, String tagValue) {
        return new TagFilter(tagKey, tagValue, Condition.EQUALS);
    }

    public static TagFilter like(String tagKey, String tagValue) {
        return new TagFilter(tagKey, "%" + tagValue + "%", Condition.LIKE);
    }

    public static TagFilter or(TagFilter... tagFilters) {
        if (Objects.isNull(tagFilters) || tagFilters.length == 0) {
            return null;
        }
        return new TagFilter(Condition.OR, Arrays.asList(tagFilters));
    }

    public static TagFilter and(TagFilter... tagFilters) {
        if (Objects.isNull(tagFilters) || tagFilters.length == 0) {
            return null;
        }
        return new TagFilter(Condition.AND, Arrays.asList(tagFilters));
    }

    /**
     * {
     *     "condition" : "AND",
     *     "tagFilters" : [
     *       {
     *         "condition" : "OR",
     *         "tagFilters" : [
     *           {
     *             "tagKey" : "UNDER_MAGIC",
     *             "condition" : "LIKE",
     *             "tagValue" : "OS"
     *           },
     *           {
     *             "tagKey" : "SYSTEM_NAME",
     *             "condition" : "EQUALS",
     *             "tagValue" : "xx系统"
     *           }
     *         ]
     *       },
     *       {
     *         "tagKey" : "OS_TYPE",
     *         "condition" : "EQUALS",
     *         "tagValue" : "1"
     *       }
     *     ]
     *   }
     * @return
     */
    public List<String> tagKeyCombination() {
        List<String> result = new ArrayList<>();
        if (CollectionUtil.isEmpty(this.tagFilters)) {
            result.add(this.tagKey);
            return result;
        }
        result = this.tagFilters.get(0).tagKeyCombination();
        for (int i = 1; i < this.tagFilters.size(); i++) {
            if (!Condition.OR.equals(this.condition) && !Condition.AND.equals(this.condition)) {
                continue;
            }
            List<String> newResult = new ArrayList<>();
            List<String> tempList = this.tagFilters.get(i).tagKeyCombination();
            if (Condition.OR.equals(this.condition)) {
                newResult.addAll(result);
                newResult.addAll(tempList);
            }
            for (String tagKey1 : result) {
                for (String tagKey2 : tempList) {
                    List<String> list = new ArrayList<>();
                    list.addAll(Arrays.asList(tagKey1.split(",")));
                    list.addAll(Arrays.asList(tagKey2.split(",")));
                    List<String> sorted = CollectionUtil.sort(list, String::compareTo);
                    newResult.add(StringUtils.join(sorted, ","));
                }
            }
            result = newResult;
        }
        return new ArrayList<>(new HashSet<>(result));
    }

    public List<TagFilter> toAll() {
        List<TagFilter> tagFilters = new ArrayList<>();
        if (CollectionUtil.isEmpty(this.tagFilters)) {
            tagFilters.add(this);
            return tagFilters;
        }
        for (TagFilter tagFilter : this.tagFilters) {
            tagFilters.addAll(tagFilter.toAll());
        }
        return tagFilters;
    }


    public static void main(String[] agrs) {
        //TagFilter tagFilter = TagFilter.and(
        //        TagFilter.or(
        //                TagFilter.equals("tag3", "value3"),
        //                TagFilter.like("tag1", "value1"),
        //                TagFilter.equals("tag1", "value2")
        //        ),
        //        TagFilter.equals("tag4", "value4"),
        //        TagFilter.equals("tag5", "value5"));
        //System.out.println(JSON.toJSONString(tagFilter));
        TagFilter tagFilter = JSONObject.parseObject(QueryJSON.tagFilter, TagFilter.class);
        List<String> tags = tagFilter.tagKeyCombination();
        System.out.println(StringUtils.joinWith(",", tags));

        List<TagFilter> allTagFilter = tagFilter.toAll();
        for (TagFilter tagFilter1 : allTagFilter) {
            System.out.println(String.format("key = %s, value = %s", tagFilter1.getTagKey(), tagFilter1.getTagValue()));
        }
    }
}
