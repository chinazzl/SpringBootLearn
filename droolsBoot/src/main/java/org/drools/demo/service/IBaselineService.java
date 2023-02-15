package org.drools.demo.service;

import org.drools.demo.Entity.bo.BaselineBO;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2023/2/15 15:27
 * @Description:
 */
public interface IBaselineService {

    /**
     * 通过ip计算一个月内，每个采集时段的基线数据
     *
     * @param ip
     * @return
     */
    BaselineBO getCpuBaseline(String ip);

}
