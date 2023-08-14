package org.template.dto;

import lombok.Data;

import java.util.List;

/**********************************
 * @author zhang zhao lin
 * @date 2023年08月14日 14:38
 * @Description:
 **********************************/
@Data
public class QueryBO {

    private List<String> targets;
    private String targetType;
    private List<String> queryTags;
    private List<TagFilter> tagFilters;
}
