package com.simpleWeb.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import com.google.common.collect.Lists;
import com.simpleWeb.config.MagicDataSourceManager;
import com.simpleWeb.entity.request.PageQueryRequest;
import com.simpleWeb.entity.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author: zhaolin
 * @Date: 2025/8/16
 * @Description:
 **/
@Component
@Slf4j
public class UniversalDataCollectionService <T>{

    @Resource
    private MagicDataSourceManager magicDataSourceManager;

    @Resource
    ThreadPoolTaskExecutor threadExecutor;


    public PageResult<List<T>> collectDatas(PageQueryRequest request,Supplier<T> instanceSupplier) {
        Set<String> dataSourceNames = magicDataSourceManager.getDataSourceNames();
        List<CompletableFuture<List<T>>> mainInfo = dataSourceNames.stream()
                .map(dsName -> CompletableFuture
                        .supplyAsync(() -> {
                            List<T> result = new ArrayList<>();
                            streamCollectTableDataWithPage(dsName, "main_info", result::addAll,instanceSupplier);
                            return result;
                        })
                        .exceptionally(ex -> {
                            log.error("数据源{},分页采集失败", dsName, ex);
                            return Lists.newArrayList();
                        }))
                .collect(Collectors.toList());


        //等待所有任务完成
        List<T> collect = mainInfo.stream()
                .map(CompletableFuture::join)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return PageResult.of(request, 0L, collect);

    }

    public Map<String, PageResult<List<T>>> collectAllTableDataWithPage(PageQueryRequest request, Supplier<T> instanceSupplier) {
        Set<String> dataSourceNames = magicDataSourceManager.getDataSourceNames();
        List<CompletableFuture<Pair<String, PageResult<List<T>>>>> collect = dataSourceNames.stream()
                .map(dsName -> CompletableFuture
                        .supplyAsync(() -> {
                            PageQueryRequest dsRequest = cloneRequest(request);
                            dsRequest.setDataSourceName(dsName);
                            PageResult<List<T>> listPageResult = collectTableDataWithPage(dsName, dsRequest,instanceSupplier);
                            return Pair.of(dsName, listPageResult);
                        })
                        .exceptionally(ex -> {
                            log.error("数据源{},分页采集失败", dsName, ex);
                            return Pair.of(dsName, PageResult.empty(request));
                        }))
                .collect(Collectors.toList());
        //等待所有任务完成
        Map<String, PageResult<List<T>>> resultMap = collect.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        return resultMap;
    }

    /**
     * 从指定数据源中分页采集数据
     */
    private PageResult<List<T>> collectTableDataWithPage(String dataSourceName,PageQueryRequest pageQueryRequest,Supplier<T>  instanceSupplier) {
        try {
            return magicDataSourceManager.withSqlSession(dataSourceName,sqlSession -> {
                Long total = 0L;
                int pageNum = 0;
                List<T> data;
                String tableName = pageQueryRequest.getTableName();
                    if (StringUtils.isEmpty(tableName)) {
                        throw new RuntimeException("表名不允许为空");
                    }
                    data = executeTablePageQuery(sqlSession, tableName, pageQueryRequest,instanceSupplier);
                    if (pageQueryRequest.getNeedCount()) {
                        total = executeTableCountQuery(sqlSession, tableName, pageQueryRequest);
                    }
                PageResult<List<T>> result = PageResult.of(pageQueryRequest, total, data);
                return result;
            });
        }catch (Exception e){
            log.error("数据源 {},分页查询失败",dataSourceName,e);
            return PageResult.empty(pageQueryRequest);
        }
    }

    /**
     * 流式采集
     * @param dataSourceName
     * @param pageQueryRequest
     */
    public void streamCollectTableDataWithPage(String dataSourceName,String tableName,Consumer<List<T>> consumer,Supplier<T> instanceSupplier) {

        try {
            Map<String,Object> whereMap = new HashMap<>();
            whereMap.put("system_id","0001");
            magicDataSourceManager.withSqlSession(dataSourceName,sqlSession -> {
                int pageNum = 1;
                int pageSize = 1;
                List<T> data;
                do {
                    PageQueryRequest pageQueryRequest = PageQueryRequest.builder()
                            .pageNum(pageNum)
                            .pageSize(pageSize)
                            .dataSourceName(dataSourceName)
                            .tableName(tableName)
                            .needCount(false)
                            .whereParams(whereMap)
                            .orderBy("id")
                            .sortDirection("asc")
                            .build();
                    data = executeTablePageQuery(sqlSession, tableName, pageQueryRequest,instanceSupplier);
                    if (CollectionUtil.isNotEmpty(data)) {
                        try {
                            consumer.accept(data);
                        }catch (Exception e){
                            log.error("处理批次数据失败，数据源：{},表：{},页数：{}",dataSourceName,tableName,pageNum,e);
                            throw e;
                        }
                    }
                    pageNum++;
                } while (data.size() == pageSize);
                return null;
            });

        }catch (Exception e){
            log.error("数据源 {},分页查询失败",dataSourceName,e);
            throw new  RuntimeException("采集数据失败",e);
        }
    }


    private List<T> executeTablePageQuery(SqlSession sqlSession, String tableName, PageQueryRequest request,Supplier<T> instanceSupplier) {
        StringBuilder sql = new StringBuilder("select * from " + tableName);
        //添加where条件
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> whereMap = new HashMap<>();
        if (request.getWhereParams() != null && !request.getWhereParams().isEmpty()) {
            sql.append(" where ");
            request.getWhereParams().forEach((key, value) -> {
                sql.append(key).append(" =#{").append(value).append("} and ");
                whereMap.put(key, value);
            });
            params.put("whereParams", whereMap);
            // 移除最后的 and
            sql.setLength(sql.length() - 5);
        }
        // 添加排序
        if (StringUtils.isNotBlank(request.getOrderBy())) {
            sql.append(" order by ")
                    .append(request.getOrderBy())
                    .append(" ")
                    .append(request.getSortDirection());
        }
        //添加分页
        sql.append(" limit ").append(request.getPageSize())
                .append(" offset ").append(request.getOffset());
        params.put("sql", sql.toString());
        params.put("tableName", tableName);
        params.put("pageSize", request.getPageSize());
        params.put("offset", request.getOffset());
        log.info("数据源：{},表名：{},sql=> {}",request.getDataSourceName(),tableName,sql.toString());
        log.info("参数：{},当前页：{}，每页显示{}条",request.getWhereParams(), request.getOffset(), request.getPageSize());
//        return sqlSession.selectList("selectDynamic", params);
        List<Map<String,Object>> selectWithParams = sqlSession.selectList("selectWithParams", params);
        List<T> collect = selectWithParams.stream()
                .map(m -> BeanUtil.fillBeanWithMap(m, instanceSupplier.get(), true))
                .collect(Collectors.toList());
        return collect;
    }

    private Long executeTableCountQuery(SqlSession sqlSession, String tableName, PageQueryRequest request) {
        StringBuilder sql = new StringBuilder("select count(*) from " + tableName);
        Map<String, Object> params = new HashMap<>();
        if (request.getWhereParams() != null && !request.getWhereParams().isEmpty()) {
            sql.append(" where ");
            request.getWhereParams().forEach((key, value) -> {
                sql.append(key).append(" =#{").append(value).append("} and ");
                params.put(key, value);
            });
            sql.setLength(sql.length() - 5);
        }
        params.put("sql", sql.toString());
        Map<String, Object> result = sqlSession.selectOne("selectCount", params);
        return result != null ? (Long) result.get("count") : 0L;
    }

    // 克隆请求对象
    private PageQueryRequest cloneRequest(PageQueryRequest original) {
        return PageQueryRequest.builder()
                .pageNum(original.getPageNum())
                .pageSize(original.getPageSize())
                .tableName(original.getTableName())
                .customSql(original.getCustomSql())
                .whereParams(original.getWhereParams() != null ?
                        new HashMap<>(original.getWhereParams()) : null)
                .orderBy(original.getOrderBy())
                .sortDirection(original.getSortDirection())
                .needCount(original.getNeedCount())
                .build();
    }

    /**
     * 验证SQL安全性（基础检查）
     */
    private void validateSqlSecurity(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL不能为空");
        }

        String upperSql = sql.toUpperCase();
        String[] dangerousKeywords = {"DROP", "DELETE", "UPDATE", "INSERT", "CREATE", "ALTER", "TRUNCATE"};

        for (String keyword : dangerousKeywords) {
            if (upperSql.contains(keyword)) {
                throw new SecurityException("SQL包含危险关键词: " + keyword);
            }
        }
    }

}
