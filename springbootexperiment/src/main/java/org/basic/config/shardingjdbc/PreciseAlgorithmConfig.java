package org.basic.config.shardingjdbc;

import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/8/2 14:46
 * @Description: 设置 =,in 的算法策略
 */
public class PreciseAlgorithmConfig implements PreciseShardingAlgorithm<Integer> {

    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<Integer> preciseShardingValue) {
        Map<Range<Comparable<Integer>>, String> idRangeTableNameMap = new HashMap<>();
        idRangeTableNameMap.put(Range.closed(1, 3), "orders_2");
        idRangeTableNameMap.put(Range.atLeast(4), "orders_1");
        System.out.println(idRangeTableNameMap);
        for (Map.Entry<Range<Comparable<Integer>>, String> idRangeEntry : idRangeTableNameMap.entrySet()) {
            final Range<Comparable<Integer>> idRange = idRangeEntry.getKey();
            final String tableName = idRangeEntry.getValue();
            final Comparable<Integer> id = preciseShardingValue.getValue();
            if (idRange.contains(id)) {
                System.out.printf("准确路由，id %s,tableName %s%n",id,tableName);
                return tableName;
            }
        }
        return null;
    }
}
