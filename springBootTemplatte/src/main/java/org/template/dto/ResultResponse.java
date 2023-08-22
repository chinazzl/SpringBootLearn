package org.template.dto;

import lombok.Data;

import java.util.List;

/**********************************
 * @author zhang zhao lin
 * @date 2023年08月14日 14:49
 * @Description:
 **********************************/
@Data
public class ResultResponse {
    private int page;
    private int pageSize;
    private int total;
    private List<Details> listData;

    @Data
    public class Details {
        private String target;
        private String tagetType;
        private List<Tag> tags;
    }

    @Data
    public class Tag {
        private String targetId;
        private String targetKey;
        private String valueText;
        private String valueType;
    }
}


