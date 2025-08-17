package com.simpleWeb.scheduler;

import com.simpleWeb.constant.VmProvider;
import com.simpleWeb.entity.db.third.MainInfoDO;
import com.simpleWeb.entity.request.PageQueryRequest;
import com.simpleWeb.entity.vo.PageResult;
import com.simpleWeb.service.UniversalDataCollectionService;
import com.simpleWeb.service.VmResourceService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author: zhaolin
 * @Date: 2025/8/17
 * @Description:
 **/
@Component
@Slf4j
public class ThirdCollectJob implements Job {

    @Autowired
    Scheduler scheduler;

    @Autowired
    UniversalDataCollectionService universalDataCollectionService;

    private static final String[] VM_PROVIDERS = new String[]{
            VmProvider.VMWARE,VmProvider.FUSION_CLOUD
    };

    @PostConstruct
    public void initJob() {
        JobDetail jobDetail = JobBuilder.newJob(this.getClass())
                .withIdentity("Third_Job")
                .storeDurably()
                .build();
        // 创建触发器
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("Third_Trigger")
                .forJob(jobDetail)
                .withSchedule(CronScheduleBuilder.cronSchedule("0/30 * 1 * * ?"))
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
        PageQueryRequest pageQueryRequest = new PageQueryRequest();
        PageResult<List<Map<String, Object>>> listPageResult = universalDataCollectionService.collectDatas(pageQueryRequest, MainInfoDO::new);
        log.info(listPageResult.toString());
    }
}
