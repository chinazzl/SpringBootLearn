package com.upsb.load;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 启动完成后按 application.yml 中的 load.* 自动压到指定数值。
 * 如需临时开机不压：启动参数加 --load.enabled=false
 */
@Component
@Order(10)
public class LoadAutoStarter implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(LoadAutoStarter.class);

    private final LoadProperties properties;
    private final CpuLoadService cpuLoadService;
    private final MemoryLoadService memoryLoadService;

    public LoadAutoStarter(LoadProperties properties,
                           CpuLoadService cpuLoadService,
                           MemoryLoadService memoryLoadService) {
        this.properties = properties;
        this.cpuLoadService = cpuLoadService;
        this.memoryLoadService = memoryLoadService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!properties.isEnabled()) {
            log.info("load.enabled=false，跳过开机自动压测");
            return;
        }
        if (properties.getCpuPercent() > 0) {
            try {
                cpuLoadService.setTarget(properties.getCpuPercent(), properties.getCores());
            } catch (Exception e) {
                log.error("开机自动 CPU 压测失败", e);
            }
        }
        if (properties.getMemoryMegabytes() > 0) {
            try {
                memoryLoadService.setTarget(properties.getMemoryMegabytes());
            } catch (Exception e) {
                log.error("开机自动内存压测失败", e);
            }
        }
        log.info("开机自动压测完成：cpu={}%, cores={}（0=自动）, memory={}MB",
                properties.getCpuPercent(), properties.getCores(), properties.getMemoryMegabytes());
    }
}
