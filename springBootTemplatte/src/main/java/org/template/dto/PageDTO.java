package org.template.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

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
    @NotNull(message = "当前页不能为空。")
    private int page;

    /**
     * 每页显示条数
     */
    @NotNull(message = "分页大小不能为空")
    private int pageSize;

}
