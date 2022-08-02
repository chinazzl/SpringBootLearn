package org.basic.config.shardingjdbc;

import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/8/2 15:43
 * @Description: 设置 BETWEEN AND, >, <, >=, <= 范围算法策略
 */
public class RangeAlgorithmConfig implements RangeShardingAlgorithm<Comparable<Integer>> {
    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<Comparable<Integer>> rangeShardingValue) {
        Map<Range<Comparable<Integer>>, String> idRangeTableNameMap = new HashMap<>();
        idRangeTableNameMap.put(Range.closed(1, 3), "orders_2");
        idRangeTableNameMap.put(Range.atLeast(4), "orders_1");
        System.out.println(idRangeTableNameMap);
        List<String> tableNameList = new ArrayList<>();
        for (Map.Entry<Range<Comparable<Integer>>, String> idRangeEntry : idRangeTableNameMap.entrySet()) {
            final Range<Comparable<Integer>> idRange = idRangeEntry.getKey();
            final String tableName = idRangeEntry.getValue();
            final Range<Comparable<Integer>> valueRange = rangeShardingValue.getValueRange();
            // 判断两个区间是否有交集
            if (idRange.isConnected(valueRange)) {
                tableNameList.add(tableName);
            }
        }
        System.out.printf("范围路由 id %s, tableList: %s ",rangeShardingValue,tableNameList);
        return tableNameList;
    }
}
