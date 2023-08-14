package org.template.dto;

import lombok.Data;
import org.template.enums.ConditionType;

import java.util.List;

/**********************************
 * @author zhang zhao lin
 * @date 2023年08月14日 14:42
 * @Description:
 **********************************/
@Data
public class TagFilter {
    private ConditionType conditionType;
    private List<TagFilter> filters;
    private String tagKey;
    private String tagValue;
}
