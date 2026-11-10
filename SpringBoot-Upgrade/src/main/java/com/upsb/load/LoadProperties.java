package com.upsb.load;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 压测自启动配置，对应 application.yml 中的 load.*。
 *
 * <pre>
 * load:
 *   enabled: true        # 总开关，false 则开机不压
 *   cpu-percent: 25      # 开机自动压到的 CPU 利用率，0 表示不压 CPU
 *   cores: 0             # CPU 线程数，0 表示自动按本机核数
 *   memory-megabytes: 16384  # 开机自动占用的内存，单位 MB，0 表示不占
 * </pre>
 */
@Component
@ConfigurationProperties(prefix = "load")
public class LoadProperties {

    /** 总开关 */
    private boolean enabled = true;
    /** CPU 目标利用率 0~100 */
    private int cpuPercent = 0;
    /** CPU 线程数，<=0 自动 */
    private int cores = 0;
    /** 内存目标占用，单位 MB */
    private long memoryMegabytes = 0;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getCpuPercent() {
        return cpuPercent;
    }

    public void setCpuPercent(int cpuPercent) {
        this.cpuPercent = cpuPercent;
    }

    public int getCores() {
        return cores;
    }

    public void setCores(int cores) {
        this.cores = cores;
    }

    public long getMemoryMegabytes() {
        return memoryMegabytes;
    }

    public void setMemoryMegabytes(long memoryMegabytes) {
        this.memoryMegabytes = memoryMegabytes;
    }
}
