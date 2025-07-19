package com.simpleWeb.scheduler;

import cn.hutool.core.collection.CollectionUtil;
import com.simpleWeb.constant.VmProvider;
import com.simpleWeb.entity.db.cmp.CmpResourceDO;
import com.simpleWeb.entity.dto.ResourceDTO;
import com.simpleWeb.service.VmResourceService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @author: zhaolin
 * @Date: 2025/7/11
 * @Description:
 **/
@Component
@Slf4j
public class SimpleJob implements Job {

    @Autowired
    Scheduler scheduler;

    @Autowired
    VmResourceService vmResourceService;

    private static final String[] VM_PROVIDERS = new String[]{
            VmProvider.VMWARE,VmProvider.FUSION_CLOUD
    };

    @PostConstruct
    public void initJob() {
        JobDetail jobDetail = JobBuilder.newJob(this.getClass())
                .withIdentity("Simple_Job")
                .storeDurably()
                .build();
        // 创建触发器
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("Simple_Trigger")
                .forJob(jobDetail)
                .withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?"))
                .build();

        try {
            if (scheduler.checkExists(jobDetail.getKey())) {
                log.info("出现同名的jobKey：{}", jobDetail.getKey());
                scheduler.deleteJob(jobDetail.getKey());
            }
            scheduler.scheduleJob(jobDetail,trigger);
        } catch (SchedulerException e) {
            log.error("初始化触发器错误，{}",e.getMessage());
        }
    }



    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("start quartz job");
        try {
            long startTime = System.currentTimeMillis();
            Date now = new Date();
            ResourceDTO resourceDTO = new ResourceDTO();
            resourceDTO.setPageNo(1);
            resourceDTO.setPageSize(1);
            List<CompletableFuture<List<CmpResourceDO>>> resultFuture = Arrays.stream(VM_PROVIDERS).map(provider -> vmResourceService.queryResourceAsync(provider, resourceDTO, now, now))
                    .collect(Collectors.toList());
            // 等待所有采集完成
            List<CmpResourceDO> collectData = Lists.newArrayList();
            for (CompletableFuture<List<CmpResourceDO>> future : resultFuture) {
                try {
                    List<CmpResourceDO> cmpResourceDOS = future.get(10, TimeUnit.MINUTES);
                    if (CollectionUtil.isNotEmpty(cmpResourceDOS)) {
                        collectData.addAll(cmpResourceDOS);
                    }
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    log.error("采集云厂商资源失败{}", e.getMessage(), e);
                }
            }
            // 批量采集保存或者更新数据
            vmResourceService.saveOrUpdateResource(collectData);
            // TODO 存入redis
            long duration = System.currentTimeMillis() - startTime;
            log.info("云服务器资源采集完成，耗时: {}ms, 采集的厂商数: {}", duration, collectData.size());
        }catch (Exception e){
            log.error("采集云服务器资源失败", e);
            throw new RuntimeException("采集云服务器资源失败", e);
        }
    }
}
