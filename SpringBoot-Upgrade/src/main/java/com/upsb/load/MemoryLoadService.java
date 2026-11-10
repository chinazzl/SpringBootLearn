package com.upsb.load;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 内存占用发生器：通过持有堆内 byte[] 使 JVM 占用指定大小的内存。
 *
 * <p>注意：</p>
 * <ul>
 *   <li>占用的是 JVM 堆内存，机器视角的内存上涨 ≈ 堆 committed 大小；请把 JVM 的 {@code -Xmx}
 *       设得比目标大（例如目标 16G 则 {@code -Xmx18g}），否则申请会 OOM。</li>
 *   <li>分配后会逐页写入数据，保证物理内存真正被 touch（避免 lazy commit 导致 top 看不到）。</li>
 * </ul>
 */
@Service
public class MemoryLoadService {

    private static final Logger log = LoggerFactory.getLogger(MemoryLoadService.class);

    /** 单块 4MB：16G 只需 4096 个对象，兼顾分配速度与管理开销 */
    private static final int CHUNK_SIZE = 4 * 1024 * 1024;

    private final List<byte[]> holder = new ArrayList<>();
    private volatile long targetBytes = 0;

    /**
     * 设置目标内存占用（增量持有，不释放 JVM）。
     *
     * @param megabytes 目标占用 MB 数，0 表示释放
     */
    public synchronized void setTarget(long megabytes) {
        if (megabytes < 0) {
            throw new IllegalArgumentException("megabytes 不能为负数");
        }
        long target = megabytes * 1024 * 1024;
        if (target == targetBytes && !holder.isEmpty()) {
            return;
        }
        if (megabytes == 0) {
            releaseLocked();
            return;
        }
        long current = (long) holder.size() * CHUNK_SIZE;
        if (target > current) {
            long need = target - current;
            int chunks = (int) ((need + CHUNK_SIZE - 1) / CHUNK_SIZE);
            for (int i = 0; i < chunks; i++) {
                byte[] chunk;
                try {
                    chunk = new byte[CHUNK_SIZE];
                } catch (OutOfMemoryError e) {
                    log.error("内存不足，已分配 {} MB，目标 {} MB，请调大 -Xmx 后重试",
                            holder.size() * (CHUNK_SIZE / 1024 / 1024), megabytes);
                    throw new IllegalStateException(
                            "堆内存不足，已分配约 " + holder.size() * (CHUNK_SIZE / 1024 / 1024)
                                    + " MB，目标 " + megabytes + " MB。请调大 -Xmx（如 -Xmx18g）后重试。", e);
                }
                // 逐页 touch，保证 OS 层面真正 commit（每 4K 写一字节）
                for (int off = 0; off < chunk.length; off += 4096) {
                    chunk[off] = (byte) (off & 0xFF);
                }
                holder.add(chunk);
            }
        } else if (target < current) {
            // 缩容：从尾部释放
            int keepChunks = (int) (target / CHUNK_SIZE);
            while (holder.size() > keepChunks) {
                holder.remove(holder.size() - 1);
            }
        }
        targetBytes = (long) holder.size() * CHUNK_SIZE;
        log.info("内存压测已设置：目标={} MB，实际持有约 {} MB（{} 块 x 4MB）",
                megabytes, holder.size() * (CHUNK_SIZE / 1024 / 1024), holder.size());
    }

    public synchronized void release() {
        releaseLocked();
        log.info("内存压测已释放");
    }

    private void releaseLocked() {
        holder.clear();
        targetBytes = 0;
        System.gc();
    }

    public synchronized long getHoldingMegabytes() {
        return holder.size() * (CHUNK_SIZE / 1024 / 1024);
    }

    public long getTargetMegabytes() {
        return targetBytes / 1024 / 1024;
    }

    @PreDestroy
    public void destroy() {
        release();
    }
}
