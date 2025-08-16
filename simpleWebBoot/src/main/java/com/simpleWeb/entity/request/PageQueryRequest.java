package com.simpleWeb.entity.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author: zhaolin
 * @Date: 2025/8/16
 * @Description:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageQueryRequest {
    /**
     * 页码，从1开始
     */
//    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
//    @Min(value = 1, message = "每页大小必须大于0")
//    @Max(value = 10000, message = "每页大小不能超过10000")
    private Integer pageSize = 1000;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 自定义SQL
     */
    private String customSql;

    /**
     * WHERE条件参数
     */
    private Map<String, Object> whereParams;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序方向：ASC/DESC
     */
    private String sortDirection = "ASC";

    /**
     * 是否需要总数统计
     */
    private Boolean needCount = false;

    /**
     * 指定数据源名称（可选，为空则查询所有数据源）
     */
    private String dataSourceName;

    /**
     * 计算LIMIT的偏移量
     */
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }

    /**
     * 是否查询所有数据源
     */
    public boolean isQueryAllDataSources() {
        return dataSourceName == null || dataSourceName.trim().isEmpty();
    }
}
