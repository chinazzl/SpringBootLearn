package org.drools.demo.dao;

import org.dmg.pmml.baseline.Baseline;
import org.drools.demo.Entity.po.BlueOsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2023/2/15 15:34
 * @Description: 性能数据获取
 */
@Repository
public interface IBaselineDao extends JpaRepository<BlueOsData,String> {

    @Query(value = "SELECT \n" +
            "        TO_CHAR(TO_DATE(B.DATATIME, 'yyyy-mm-dd hh24:mi:ss'), 'HH24:MI') AS DMIN\n" +
            "    FROM BLUE_OS_DATA b\n" +
            "    WHERE B.IP = ?1\n" +
            "    GROUP BY TO_CHAR(TO_DATE(B.DATATIME, 'yyyy-mm-dd hh24:mi:ss'), 'HH24:MI') \n" +
            " ORDER BY DMIN DESC",nativeQuery = true)
    List<String> getTimePeriod(String ip);

    @Query(value = "SELECT bod.DATATIME,\n" +
            "       IP,\n" +
            "       TOTALUSAGE \n" +
            "FROM BLUE_OS_DATA bod\n" +
            "WHERE bod.IP = ?1\n" +
            "AND TO_CHAR(TO_DATE(bod.DATATIME, 'yyyy-mm-dd hh24:mi:ss'), 'HH24:MI') = ?2 \n" +
            " ORDER BY bod.DATATIME DESC",nativeQuery = true)
    List<BlueOsData> getPerformanceData(String ip,String dmin);
}
