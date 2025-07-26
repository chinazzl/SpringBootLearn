package base;

import com.alibaba.fastjson2.JSON;
import com.simpleWeb.Runner;
import com.simpleWeb.entity.db.primary.ResourceDO;
import com.simpleWeb.entity.db.primary.ResourcePriceDO;
import com.simpleWeb.entity.dto.AreaResourceItem;
import com.simpleWeb.entity.vo.ResourceResultVO;
import com.simpleWeb.mapper.primary.ResourceMapper;
import com.simpleWeb.mapper.primary.ResourcePriceRuleMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.collections.Lists;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.simpleWeb.constant.VmProvider.*;

/**
 * @author: zhaolin
 * @Date: 2025/7/26
 * @Description:
 **/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Runner.class)
@RunWith(SpringRunner.class)
@Slf4j
public class BaseTest {

    @Resource
    ResourcePriceRuleMapper resourcePriceRuleMapper;

    @Resource
    ResourceMapper resourceMapper;

    @Test
    public void testCalPrice() {
        List<ResourceResultVO> result = Lists.newArrayList();
        List<ResourcePriceDO> resourcePriceDOS = resourcePriceRuleMapper.resourcePriceDOList(LocalDate.now());
        log.info("resourcePriceDOS:{}", resourcePriceDOS);
        List<ResourceDO> resourceDOS = resourceMapper.selectResourceDOList();
        log.info("resourceDOS:{}", resourceDOS);
        // CPU
        ResourceResultVO resourceResultVO = new ResourceResultVO();
        resourceResultVO.setTitle("cpu总量");
        resourceResultVO.setUnit("核");
        resourceResultVO.setType("cpu");
        List<AreaResourceItem> areaResourceItems = Lists.newArrayList();
        for (ResourceDO resourceDO : resourceDOS) {
            AreaResourceItem areaResourceItem = new AreaResourceItem();
            areaResourceItem.setAreaName(resourceDO.getAreaName());
            areaResourceItem.setValue(String.valueOf(resourceDO.getCpuCore()));
            areaResourceItem.setAreaId(String.valueOf(resourceDO.getAreaId()));
            resourcePriceDOS.stream().filter(resourcePriceDO -> resourcePriceDO.getAreaName().equals(resourceDO.getAreaName()))
                    .forEach(resourcePriceDO -> {
                        String cpuArchitecture = resourceDO.getCpuArchitecture();
                        int cpuCost = 0;
                        switch (cpuArchitecture) {
                            case X86:
                                cpuCost = resourceDO.getCpuCore() * resourcePriceDO.getCpuX86();
                                break;
                            case ARM:
                                cpuCost = resourceDO.getCpuCore() * resourcePriceDO.getCpuARM();
                                break;
                            case C86:
                                cpuCost = resourceDO.getCpuCore() * resourcePriceDO.getCpuC86();
                                break;
                            default:
                        }
                        areaResourceItem.setCost(String.valueOf(cpuCost));
                    });
            areaResourceItems.add(areaResourceItem);

        }
        resourceResultVO.setItems(areaResourceItems);
        result.add(resourceResultVO);
        // memory
        // 1. 聚合只通过网络环境进行分组
        Map<String, Optional<Integer>> memoryGroup = resourceDOS.stream().collect(Collectors.groupingBy(ResourceDO::getAreaName,
                Collectors.mapping(ResourceDO::getMemory, Collectors.reducing(Integer::sum))));
        resourceResultVO = new ResourceResultVO();
        List<AreaResourceItem> memoryAreaResourceItems = Lists.newArrayList();
        resourceResultVO.setTitle("内存总量");
        resourceResultVO.setUnit("GB");
        resourceResultVO.setType("memory");
        for (ResourceDO resourceDO : resourceDOS) {
            memoryGroup.forEach((areaName, memoryOptional) -> {
                AreaResourceItem areaResourceItem = new AreaResourceItem();
                if (areaName.equals(resourceDO.getAreaName())) {
                    Integer memory = memoryOptional.orElse(0);
                    areaResourceItem.setAreaName(areaName);
                    areaResourceItem.setValue(String.valueOf(memory));
                    areaResourceItem.setAreaId(String.valueOf(resourceDO.getAreaId()));
                    Integer memoryCost = resourcePriceDOS.stream().filter(resourcePriceDO -> resourcePriceDO.getAreaName().equals(resourceDO.getAreaName()))
                            .map(ResourcePriceDO::getMemoryPrice).findFirst().orElse(0);
                    areaResourceItem.setCost(String.valueOf(memory * memoryCost));
                }
                memoryAreaResourceItems.add(areaResourceItem);
            });
        }
        resourceResultVO.setItems(memoryAreaResourceItems);
        result.add(resourceResultVO);
        log.info("result:{}", JSON.toJSONString(result));
    }
}
