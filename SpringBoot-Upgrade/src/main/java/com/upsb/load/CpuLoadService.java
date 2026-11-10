package com.upsb.load;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * CPU 负载发生器：通过“忙循环 + 休眠”的占空比方式，把若干个工作线程的 CPU 占用率
 * 控制在目标百分比附近。
 *
 * <p>原理：每个工作线程在一个周期（默认 100ms）内，前 {@code targetPercent%} 的时间做忙循环，
 * 剩余时间 sleep。这样单核占用率 ≈ target；起 {@code N = CPU 核数} 个线程，整机占用率 ≈ target。</p>
 */
@Service
public class CpuLoadService {

    private static final Logger log = LoggerFactory.getLogger(CpuLoadService.class);

    /** 单个调度周期，100ms：兼顾精度与调度开销 */
    private static final long PERIOD_NANOS = 100_000_000L;

    private final AtomicInteger targetPercent = new AtomicInteger(0);
    private final AtomicInteger workerCount = new AtomicInteger(0);
    private final AtomicBoolean running = new AtomicBoolean(false);

    private volatile List<Thread> workers = new ArrayList<>();

    /**
     * 设置目标 CPU 利用率（整机视角）。
     *
     * @param percent 0~100，0 表示停止
     * @param cores   <=0 表示自动取 availableProcessors
     */
    public synchronized void setTarget(int percent, int cores) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("cpu percent 必须在 0~100 之间");
        }
        int effectiveCores = cores <= 0 ? Runtime.getRuntime().availableProcessors() : cores;
        if (effectiveCores <= 0) {
            effectiveCores = 1;
        }
        // 先停掉旧线程
        stopLocked();

        targetPercent.set(percent);
        workerCount.set(percent == 0 ? 0 : effectiveCores);

        if (percent == 0) {
            log.info("CPU 压测已停止");
            return;
        }
        running.set(true);
        List<Thread> list = new ArrayList<>(effectiveCores);
        for (int i = 0; i < effectiveCores; i++) {
            Thread t = new Thread(this::spinLoop, "cpu-loader-" + i);
            t.setDaemon(true);
            t.start();
            list.add(t);
        }
        workers = list;
        log.info("CPU 压测已启动：目标={}%, 线程数={}, 可用核数={}", percent, effectiveCores,
                Runtime.getRuntime().availableProcessors());
    }

    public void stop() {
        synchronized (this) {
            stopLocked();
            targetPercent.set(0);
            workerCount.set(0);
        }
        log.info("CPU 压测已停止");
    }

    private void stopLocked() {
        running.set(false);
        for (Thread t : workers) {
            t.interrupt();
        }
        workers = new ArrayList<>();
    }

    private void spinLoop() {
        // 忙循环里做一点真实计算，防止 JIT 把空循环优化掉
        long x = 0;
        while (running.get() && !Thread.currentThread().isInterrupted()) {
            int percent = targetPercent.get();
            if (percent <= 0) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                continue;
            }
            long busyNanos = PERIOD_NANOS * percent / 100;
            long start = System.nanoTime();
            // busy 阶段
            while (System.nanoTime() - start < busyNanos) {
                // 简单的数学运算消耗 CPU，且结果被累加使用，避免被 JIT 消除
                x += System.nanoTime() % 7 + (long) Math.sqrt(x & 0xFFFF);
                if ((x & 0x3FFF) == 0) {
                    Thread.onSpinWait();
                }
                if (Thread.currentThread().isInterrupted() || !running.get()) {
                    return;
                }
            }
            // idle 阶段
            long elapsed = System.nanoTime() - start;
            long sleepNanos = PERIOD_NANOS - elapsed;
            if (sleepNanos > 0) {
                try {
                    long ms = sleepNanos / 1_000_000L;
                    int ns = (int) (sleepNanos % 1_000_000L);
                    Thread.sleep(ms, ns);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    public int getTargetPercent() {
        return targetPercent.get();
    }

    public int getWorkerCount() {
        return workerCount.get();
    }

    public boolean isRunning() {
        return running.get();
    }

    @PreDestroy
    public void destroy() {
        stop();
    }
}
