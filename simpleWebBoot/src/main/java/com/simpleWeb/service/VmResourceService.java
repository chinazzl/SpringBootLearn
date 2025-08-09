package com.simpleWeb.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.simpleWeb.config.ResourceCollectFactory;
import com.simpleWeb.entity.db.cmp.CmpResourceDO;
import com.simpleWeb.entity.db.primary.ResourceDO;
import com.simpleWeb.entity.dto.ResourceDTO;
import com.simpleWeb.mapper.primary.ResourceMapper;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author: zhaolin
 * @Date: 2025/7/18
 * @Description:
 **/
@Component
@Slf4j
public class VmResourceService {

    @Autowired
    private ResourceCollectFactory cmpResourceCollectFactory;

    @Autowired
    private ThreadPoolTaskExecutor threadExecutor;

    @Autowired
    private ResourceMapper resourceMapper;

    public CompletableFuture<List<CmpResourceDO>> queryResourceAsync(String provider, ResourceDTO resourceDTO,
                                                                     Date startDate, Date endTime) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("开始采集 {} 资源数据", provider);
                long startTime = System.currentTimeMillis();
                int currentPage = 1;
                boolean hasMoreData = true;
                List<CmpResourceDO> cmpResourceDOList = new ArrayList<>();
                while (hasMoreData) {
                    try {
                        PageHelper.startPage(currentPage, resourceDTO.getPageSize());
                        List<CmpResourceDO> cmpResourceDOS = cmpResourceCollectFactory.queryCmpResourceList(provider);
                        if (cmpResourceDOS.isEmpty()) {
                            log.warn("{} 资源采集返回空数据,当前页{}", provider, currentPage);
                        }
                        for (CmpResourceDO cmpResourceDO : cmpResourceDOS) {
                            cmpResourceDO.setStartTime(startDate);
                            cmpResourceDO.setEndTime(endTime);
                            long duration = System.currentTimeMillis() - startTime;
                            log.info("{} ,第{}页资源采集完成，耗时：{}ms,CPU架构：{},CPU：{},内存:{},本地磁盘：{},共享存储：{}",
                                    provider, currentPage, duration, cmpResourceDO.getCpuArchitecture(),
                                    cmpResourceDO.getCpuCore(), cmpResourceDO.getMemory(),
                                    cmpResourceDO.getLocalDisk(), cmpResourceDO.getShareDisk());
                        }
                        PageInfo<CmpResourceDO> pageInfo = new PageInfo<>(cmpResourceDOS);
                        cmpResourceDOList.addAll(cmpResourceDOS);
                        // 判断是否存在下一页
                        hasMoreData = pageInfo.isHasNextPage();
                        currentPage++;
                    } finally {
                        PageHelper.clearPage();
                    }
                }
                return cmpResourceDOList;
            } catch (Exception e) {
                log.error("采集{} 资源失败,异常：{}", provider, e.getMessage(), e);
                return null;
            } finally {
                PageHelper.clearPage();
            }
        }, threadExecutor);
    }

    public List<ResourceDO> saveOrUpdateResource(List<CmpResourceDO> cmpResourceDOS) {
        if (CollectionUtil.isEmpty(cmpResourceDOS)) {
            return Lists.newArrayList();
        }
        List<ResourceDO> insertResourceDOList = new ArrayList<>();
        List<ResourceDO> updateResourceDOList = new ArrayList<>();
        for (CmpResourceDO cmpResourceDO : cmpResourceDOS) {
            int byServiceTypeCnt = resourceMapper.findByServiceType(cmpResourceDO.getServiceType());
            if (byServiceTypeCnt <= 0) {
                ResourceDO resourceDO = BeanUtil.copyProperties(cmpResourceDO, ResourceDO.class);
                insertResourceDOList.add(resourceDO);
            } else {
                ResourceDO resourceDO = BeanUtil.copyProperties(cmpResourceDO, ResourceDO.class);
                updateResourceDOList.add(resourceDO);
            }
        }
        if (CollectionUtil.isNotEmpty(insertResourceDOList)) {
            resourceMapper.insert(insertResourceDOList);
            // TODO 添加判断是否插入成功
            return insertResourceDOList;
        }
        if (CollectionUtil.isNotEmpty(updateResourceDOList)) {
            int i = resourceMapper.batchUpdate(updateResourceDOList);
            log.info("{}",i);
//            for (ResourceDO resourceDO : updateResourceDOList) {
//                int update = resourceMapper.update(resourceDO);
//                log.info("更新了{}条数据，云厂商为{}",update,resourceDO.getServiceType());
//            }
            return updateResourceDOList;
        }
        return Lists.newArrayList();
    }
}
