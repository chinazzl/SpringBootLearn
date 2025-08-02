package dao;

import com.google.common.collect.Lists;
import com.simpleWeb.Runner;
import com.simpleWeb.entity.db.cmp.CmpResourceDO;
import com.simpleWeb.entity.db.primary.ResourceDO;
import com.simpleWeb.entity.dto.YaxisDTO;
import com.simpleWeb.mapper.cmp.CmpResourceMapper;
import com.simpleWeb.mapper.primary.ResourceMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.sql.DataSourceDefinition;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author: zhaolin
 * @Date: 2025/7/17
 * @Description:
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Runner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DaoTest {

    @Autowired
    private CmpResourceMapper cmpResourceMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Test
    public void testDao() {
        List<ResourceDO> resourceDOS = resourceMapper.queryResourceGroupTime();
        List<LocalDate> periousDays = getPeriousDay(7);
        List<String> xAiaxs = resourceDOS.stream()
                .filter(resourceDO -> StringUtils.isNotBlank(resourceDO.getCpuArchitecture()))
                .map(ResourceDO::getCpuArchitecture)
                .distinct()
                .collect(Collectors.toList());
        List<YaxisDTO> yaxisDTOS = Lists.newArrayList();
        Map<LocalDate, List<ResourceDO>> groupDate = resourceDOS.stream().collect(Collectors.groupingBy(ResourceDO::getCreateDate));
        for (LocalDate periousDay : periousDays) {
            for (Map.Entry<LocalDate, List<ResourceDO>> entry : groupDate.entrySet()) {
                if (entry.getKey().isEqual(periousDay)) {
                    entry.getValue().forEach(cmpResourceDO -> {
                        for (String xAiax : xAiaxs) {
                            YaxisDTO yaxisDTO = new YaxisDTO();
                            if (cmpResourceDO.getCpuArchitecture().equals(xAiax)) {
                                yaxisDTO.setTime(periousDay);
                                yaxisDTO.setVal(cmpResourceDO.getCpuCore());
                            } else {
                                yaxisDTO.setTime(periousDay);
                                yaxisDTO.setVal(0);
                            }
                            yaxisDTOS.add(yaxisDTO);
                        }
                    });
                }else {
                    for (String xAiax : xAiaxs) {
                        YaxisDTO yaxisDTO = new YaxisDTO();
                        yaxisDTO.setTime(periousDay);
                        yaxisDTO.setVal(0);
                        yaxisDTOS.add(yaxisDTO);
                    }
                }
                System.out.println(yaxisDTOS);
            }
        }


    }


    private List<LocalDate> getPeriousDay(int n) {
        LocalDate now = LocalDate.now();
        return IntStream.rangeClosed(1,n)
                .mapToObj(now::minusDays)
                .collect(Collectors.toList());
    }
}
