package org.template.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;

/**********************************
 * @author zhang zhao lin
 * @date 2023年08月14日 14:38
 * @Description:
 **********************************/
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryBO extends PageDTO{

    private List<String> targets;
    @NotNull(message = "目标查询类型不能为空。",groups = Query.class)
    private String targetType;
    @NotNull(message = "待查询的标签不能为空",groups = Query.class)
    private List<String> queryTags;
    private TagFilter tagFilters;

    public interface Query {

    }
}
