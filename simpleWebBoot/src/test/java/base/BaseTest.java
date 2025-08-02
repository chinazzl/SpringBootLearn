package base;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.simpleWeb.Runner;
import com.simpleWeb.entity.db.primary.ResourceDO;
import com.simpleWeb.entity.db.primary.ResourcePriceDO;
import com.simpleWeb.entity.dto.AreaResourceItem;
import com.simpleWeb.entity.dto.PriceRuleDTO;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
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
            PriceRuleDTO lastPriceRuleDTO = getLastPriceRuleDTO((long) resourceDO.getAreaId());
            String cpuArchitecture = Optional.ofNullable(resourceDO.getCpuArchitecture()).orElse("");
            BigDecimal cpuCost = BigDecimal.ZERO;
            switch (cpuArchitecture) {
                case X86:
                    if (Objects.nonNull(lastPriceRuleDTO.getCpuX86Price()) && resourceDO.getCpuCore() > 0) {
                        cpuCost = lastPriceRuleDTO.getCpuX86Price().multiply(new BigDecimal(resourceDO.getCpuCore()));
                    }
                    break;
                case ARM:
                    if (Objects.nonNull(lastPriceRuleDTO.getCpuARMPrice()) && resourceDO.getCpuCore() > 0) {
                        cpuCost = lastPriceRuleDTO.getCpuARMPrice().multiply(new BigDecimal(resourceDO.getCpuCore()));
                    }
                    break;
                case C86:
                    if (Objects.nonNull(lastPriceRuleDTO.getCpuC86Price()) && resourceDO.getCpuCore() > 0) {
                        cpuCost = lastPriceRuleDTO.getCpuC86Price().multiply(new BigDecimal(resourceDO.getCpuCore()));
                    }
                    break;
                default:
            }
            areaResourceItem.setCost(String.valueOf(cpuCost));
            areaResourceItems.add(areaResourceItem);
        }
        resourceResultVO.setItems(areaResourceItems);
        result.add(resourceResultVO);
        // memory
        // 1. 聚合只通过网络环境进行分组
        Map<Long, Optional<Integer>> memoryGroup = resourceDOS.stream().collect(Collectors.groupingBy(ResourceDO::getAreaId,
                Collectors.mapping(ResourceDO::getMemory, Collectors.reducing(Integer::sum))));
        resourceResultVO = new ResourceResultVO();
        List<AreaResourceItem> memoryAreaResourceItems = Lists.newArrayList();
        resourceResultVO.setTitle("内存总量");
        resourceResultVO.setUnit("GB");
        resourceResultVO.setType("memory");
        for (ResourceDO resourceDO : resourceDOS) {
            AreaResourceItem areaResourceItem = new AreaResourceItem();
            PriceRuleDTO lastPriceRuleDTO = getLastPriceRuleDTO((long) resourceDO.getAreaId());
            areaResourceItem.setAreaName(resourceDO.getAreaName());
            areaResourceItem.setValue(String.valueOf(resourceDO.getMemory()));
            areaResourceItem.setAreaId(String.valueOf(resourceDO.getAreaId()));
            if (Objects.nonNull(lastPriceRuleDTO.getMemoryPriice()) && resourceDO.getMemory() > 0) {
                BigDecimal memoryCost = lastPriceRuleDTO.getMemoryPriice().multiply(new BigDecimal(resourceDO.getMemory()));
                areaResourceItem.setCost(String.valueOf(memoryCost));
            } else {
                areaResourceItem.setCost("0");
            }
            memoryAreaResourceItems.add(areaResourceItem);
        }
        resourceResultVO.setItems(memoryAreaResourceItems);
        result.add(resourceResultVO);
        log.info("result:{}", JSON.toJSONString(result));
    }

    private PriceRuleDTO getLastPriceRuleDTO(Long areaId) {
        PriceRuleDTO priceRuleDTO = new PriceRuleDTO();
        // 1. 获取所有的计费信息，然后根据areaName 进行分组
        List<ResourcePriceDO> resourcePriceDOS = resourcePriceRuleMapper.resourcePriceDOListByAreaId(areaId);
        log.info("resourcePriceDOS:{}", resourcePriceDOS);
        // 2. 根据分组后获取计费规则中每个字段的最新的数据
        if (CollectionUtil.isEmpty(resourcePriceDOS)) {
            return new PriceRuleDTO(areaId);
        }
        // 3. 构建DTO
        getLatestResourcePrice(resourcePriceDOS, ResourcePriceDO::getCpuX86)
                .ifPresent(resourcePriceDO -> {
                    priceRuleDTO.setCpuX86Price(resourcePriceDO.getCpuX86());
                });
        getLatestResourcePrice(resourcePriceDOS, ResourcePriceDO::getCpuARM)
                .ifPresent(resourcePriceDO -> {
                    priceRuleDTO.setCpuARMPrice(resourcePriceDO.getCpuARM());
                });
        getLatestResourcePrice(resourcePriceDOS, ResourcePriceDO::getCpuC86)
                .ifPresent(resourcePriceDO -> {
                    priceRuleDTO.setCpuC86Price(resourcePriceDO.getCpuC86());
                });
        getLatestResourcePrice(resourcePriceDOS, ResourcePriceDO::getMemoryPrice)
                .ifPresent(resourcePriceDO -> {
                    priceRuleDTO.setMemoryPriice(resourcePriceDO.getMemoryPrice());
                });
        getLatestResourcePrice(resourcePriceDOS, ResourcePriceDO::getLocalDiskPrice)
                .ifPresent(resourcePriceDO -> {
                    priceRuleDTO.setLocalDiskPrice(resourcePriceDO.getLocalDiskPrice());
                });
        getLatestResourcePrice(resourcePriceDOS, ResourcePriceDO::getNasPrice)
                .ifPresent(resourcePriceDO -> {
                    priceRuleDTO.setNasDiskPrice(resourcePriceDO.getNasPrice());
                });
        priceRuleDTO.setAreaId(areaId);
        return priceRuleDTO;
    }

    private Optional<ResourcePriceDO> getLatestResourcePrice(List<ResourcePriceDO> resourcePriceDOS,
                                                             Function<ResourcePriceDO, BigDecimal> function) {
        return resourcePriceDOS.stream()
                .filter(resourcePriceDO -> function.apply(resourcePriceDO) != null)
                .findFirst();
    }

}
