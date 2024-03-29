package org.basic.config.shardingjdbc;

import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/8/3 14:20
 * @Description: 混合模式分片算法
 */
public class ComplexAlgorithmConfig implements ComplexKeysShardingAlgorithm<Comparable<Integer>> {
    @Override
    public Collection<String> doSharding(Collection availableTargetNames, ComplexKeysShardingValue shardingValue) {
        List<String> tableNames = new ArrayList<>();
        // =、in处理
        final Map columnNameAndShardingValuesMap = shardingValue.getColumnNameAndShardingValuesMap();
        Collection<Integer> storage_types = (Collection<Integer>) columnNameAndShardingValuesMap.get("storage_type");
        Collection<Long> ids = (Collection<Long>) columnNameAndShardingValuesMap.get("id");
        if (storage_types != null) {
            for (Integer storage_type : storage_types) {
                if (storage_type == 0) {
                    if (ids != null) {
                        for (Long id : ids) {
                            if (id % 2 == 0) {
                                tableNames.add("t_file_0");
                            } else {
                                tableNames.add("t_file_1");
                            }
                        }
                    } else {
                        tableNames.add("t_file_0");
                        tableNames.add("t_file_1");
                    }
                } else if (storage_type == 1) {
                    tableNames.add("t_file_2");
                }
            }
        }
        // 范围分片,map存放的是指定 查询列的列明集合
        final Map columnNameAndRangeValuesMap = shardingValue.getColumnNameAndRangeValuesMap();
        Range<Integer> range_ids = (Range<Integer>) columnNameAndRangeValuesMap.get("id");
        rangeValueColumnName(range_ids,tableNames);
        System.out.printf("路由信息,tableNames：%s, id值：%s, storage_type值：%s%n", tableNames, ids, storage_types);
        return tableNames.isEmpty() ? availableTargetNames : tableNames;
    }

    private List<String> rangeValueColumnName(Range<Integer> rangeIds, List<String> tableNames) {
        Map<Range<Integer>,String> pre = new HashMap<>();
        pre.put(Range.closedOpen(0,1),"t_file_0");
        pre.put(Range.atLeast(1),"t_file_1");
        if (!tableNames.contains("t_file_2")) {
            for (Map.Entry<Range<Integer>, String> rangeStringEntry : pre.entrySet()) {
                Range<Integer> key = rangeStringEntry.getKey();
                if (key.isConnected(rangeIds)) {
                    tableNames.add(rangeStringEntry.getValue());
                }
            }
        }
        return tableNames;
    }
}
