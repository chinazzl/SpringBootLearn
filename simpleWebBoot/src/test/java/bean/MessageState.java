package bean;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: zhaolin
 * @Date: 2025/2/19
 * @Description:
 **/
public class MessageState {

    private volatile long firstSeenTime;
    private final AtomicInteger count;
    private final ReentrantLock lock;

    public MessageState() {
        this.firstSeenTime = System.currentTimeMillis();
        this.count = new AtomicInteger(1);
        this.lock = new ReentrantLock();
    }

    public boolean isDuplicate(Duration window) {
        long now = System.currentTimeMillis();
        lock.lock();
        try {
            if (now - firstSeenTime > window.toMillis()) {
                // 超过时间窗口，重置状态
                firstSeenTime = now;
                count.set(1);
                return false;
            }
            // 在时间窗口内，增加计数
            count.incrementAndGet();
            return true;
        } finally {
            lock.unlock();
        }
    }

    public int getCount() {
        return count.get();
    }

    public long getFirstSeenTime() {
        return firstSeenTime;
    }
}
