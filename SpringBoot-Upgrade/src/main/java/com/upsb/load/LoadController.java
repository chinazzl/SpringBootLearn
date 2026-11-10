package com.upsb.load;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统负载压测控制器：动态调节本机 CPU 利用率与内存占用。
 *
 * <p>示例（context-path=/up）：</p>
 * <pre>
 * # CPU 25%
 * curl -X POST localhost:9000/up/api/load/cpu \
 *   -H 'Content-Type: application/json' -d '{"percent":25}'
 *
 * # 内存 16G（16384 MB）
 * curl -X POST localhost:9000/up/api/load/memory \
 *   -H 'Content-Type: application/json' -d '{"megabytes":16384}'
 *
 * # 一次全设
 * curl -X POST localhost:9000/up/api/load/all \
 *   -H 'Content-Type: application/json' -d '{"cpuPercent":25,"memoryMegabytes":16384}'
 *
 * # 查看状态
 * curl localhost:9000/up/api/load/status
 *
 * # 停止
 * curl -X DELETE localhost:9000/up/api/load/cpu
 * curl -X DELETE localhost:9000/up/api/load/memory
 * curl -X DELETE localhost:9000/up/api/load/all
 * </pre>
 */
@RestController
@RequestMapping("/api/load")
public class LoadController {

    private final CpuLoadService cpuLoadService;
    private final MemoryLoadService memoryLoadService;

    public LoadController(CpuLoadService cpuLoadService, MemoryLoadService memoryLoadService) {
        this.cpuLoadService = cpuLoadService;
        this.memoryLoadService = memoryLoadService;
    }

    /** 设置 CPU 目标利用率。cores 不传或 <=0 则自动按本机核数起线程。 */
    @PostMapping("/cpu")
    public Map<String, Object> setCpu(@RequestBody Map<String, Object> body) {
        int percent = toInt(body.get("percent"), body.get("cpuPercent"));
        int cores = toIntOrDefault(body.get("cores"), 0);
        cpuLoadService.setTarget(percent, cores);
        return status();
    }

    @DeleteMapping("/cpu")
    public Map<String, Object> stopCpu() {
        cpuLoadService.stop();
        return status();
    }

    /** 设置内存目标占用，单位 MB（16G = 16384）。0 表示释放。 */
    @PostMapping("/memory")
    public Map<String, Object> setMemory(@RequestBody Map<String, Object> body) {
        long mb = toLong(body.get("megabytes"), body.get("memoryMegabytes"), body.get("memoryMb"));
        memoryLoadService.setTarget(mb);
        return status();
    }

    @DeleteMapping("/memory")
    public Map<String, Object> releaseMemory() {
        memoryLoadService.release();
        return status();
    }

    /** 同时设置 CPU + 内存，参数 cpuPercent(0~100)、memoryMegabytes。缺哪个就不动哪个。 */
    @PostMapping("/all")
    public Map<String, Object> setAll(@RequestBody Map<String, Object> body) {
        if (body.containsKey("cpuPercent") || body.containsKey("percent")) {
            int percent = toInt(body.get("percent"), body.get("cpuPercent"));
            int cores = toIntOrDefault(body.get("cores"), 0);
            cpuLoadService.setTarget(percent, cores);
        }
        if (body.containsKey("memoryMegabytes") || body.containsKey("megabytes") || body.containsKey("memoryMb")) {
            long mb = toLong(body.get("megabytes"), body.get("memoryMegabytes"), body.get("memoryMb"));
            memoryLoadService.setTarget(mb);
        }
        return status();
    }

    @DeleteMapping("/all")
    public Map<String, Object> stopAll() {
        cpuLoadService.stop();
        memoryLoadService.release();
        return status();
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> result = new HashMap<>();
        int cpus = Runtime.getRuntime().availableProcessors();

        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        double systemLoadAvg = os.getSystemLoadAverage();
        double cpuLoad = -1;
        if (os instanceof com.sun.management.OperatingSystemMXBean sunOs) {
            cpuLoad = sunOs.getCpuLoad(); // 0~1，整机最近时刻利用率
        }

        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        long heapUsed = mem.getHeapMemoryUsage().getUsed();
        long heapMax = mem.getHeapMemoryUsage().getMax();

        Map<String, Object> cpu = new HashMap<>();
        cpu.put("running", cpuLoadService.isRunning());
        cpu.put("targetPercent", cpuLoadService.getTargetPercent());
        cpu.put("workerThreads", cpuLoadService.getWorkerCount());
        cpu.put("availableProcessors", cpus);
        cpu.put("systemCpuLoad", cpuLoad < 0 ? null : Math.round(cpuLoad * 1000) / 10.0);
        cpu.put("systemLoadAverage", systemLoadAvg);
        result.put("cpu", cpu);

        Map<String, Object> memory = new HashMap<>();
        memory.put("targetMegabytes", memoryLoadService.getTargetMegabytes());
        memory.put("holdingMegabytes", memoryLoadService.getHoldingMegabytes());
        memory.put("heapUsedMegabytes", heapUsed / 1024 / 1024);
        memory.put("heapMaxMegabytes", heapMax < 0 ? null : heapMax / 1024 / 1024);
        result.put("memory", memory);

        result.put("hint", "内存目标 16G 请传 16384；JVM 启动需加 -Xmx18g 左右，堆要大于目标");
        return result;
    }

    private static int toInt(Object... candidates) {
        for (Object c : candidates) {
            if (c instanceof Number n) {
                return n.intValue();
            }
            if (c instanceof String s && !s.isBlank()) {
                return Integer.parseInt(s.trim());
            }
        }
        throw new IllegalArgumentException("缺少数值参数（cpu 请传 percent / cpuPercent，0~100）");
    }

    private static int toIntOrDefault(Object value, int def) {
        if (value == null) {
            return def;
        }
        if (value instanceof Number n) {
            return n.intValue();
        }
        if (value instanceof String s && !s.isBlank()) {
            return Integer.parseInt(s.trim());
        }
        return def;
    }

    private static long toLong(Object... candidates) {
        for (Object c : candidates) {
            if (c instanceof Number n) {
                return n.longValue();
            }
            if (c instanceof String s && !s.isBlank()) {
                return Long.parseLong(s.trim());
            }
        }
        throw new IllegalArgumentException("缺少数值参数（内存请传 megabytes / memoryMegabytes，单位 MB）");
    }
}
