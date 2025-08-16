package com.simpleWeb.entity.vo;

import com.simpleWeb.entity.request.PageQueryRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: zhaolin
 * @Date: 2025/8/16
 * @Description:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResult <T> implements Serializable {
    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 数据列表
     */
    private T data;

    /**
     * 是否有下一页
     */
    private Boolean hasNext;

    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;

    /**
     * 数据源名称（单数据源查询时使用）
     */
    private String dataSourceName;

    public static <T> PageResult<T> of(PageQueryRequest request, Long total, T data) {
        int totalPages = total == 0 ? 1 : (int) Math.ceil((double) total / request.getPageSize());

        return PageResult.<T>builder()
                .pageNum(request.getPageNum())
                .pageSize(request.getPageSize())
                .total(total)
                .pages(totalPages)
                .data(data)
                .hasNext(request.getPageNum() < totalPages)
                .hasPrevious(request.getPageNum() > 1)
                .dataSourceName(request.getDataSourceName())
                .build();
    }

    public static <T> PageResult<T> empty(PageQueryRequest request) {
        return of(request, 0L, null);
    }
}
