package com.simpleWeb.entity.vo;

import com.simpleWeb.entity.dto.AreaResourceItem;
import lombok.Data;

import java.util.List;

/**
 * @author: zhaolin
 * @Date: 2025/7/23
 * @Description:
 **/
@Data
public class ResourceResultVO {

    private String type;
    private String title;
    private String unit;
    private List<AreaResourceItem> items;

}
