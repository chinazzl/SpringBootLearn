package org.template.dto;

import lombok.Data;

/**********************************
 * @author zhang zhao lin
 * @date 2023年08月22日 16:27
 * @Description:
 **********************************/
@Data
public class PageDTO {

    /**
     * 当前页
     */
    private int page;

    /**
     * 每页显示条数
     */
    private int pageSize;

}
