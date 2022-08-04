package org.basic.config.shardingjdbc;

import org.apache.shardingsphere.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.hint.HintShardingValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/8/4 9:57
 * @Description:
 */
public class HintManagerConfig implements HintShardingAlgorithm<Comparable<Integer>> {

    @Override
    public Collection<String> doSharding(Collection availableTargetNames, HintShardingValue shardingValue) {
        final Object[] tables = availableTargetNames.toArray();
        List<String> result = new ArrayList<>();
        // HintManager.getInstance().addTableShardingValue放入的值都在shardingValue里面
        final Collection<Integer> tableIndexList = shardingValue.getValues();
        for (Integer tableIndex : tableIndexList) {
            result.add((String)tables[tableIndex]);
        }
        return result;
    }
}
