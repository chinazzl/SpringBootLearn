import bean.MessageState;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.stream.IntStream;

/**
 * @Author: zhang zhao lin
 * @Description:
 * @Date:Create：in 2020/6/17 21:57
 * @Modified By：
 */
@Slf4j
public class test {
    private final LoadingCache<String, MessageState> messageCache;
    private final Duration window = Duration.ofMinutes(5);

    public test() {
        this.messageCache = Caffeine.newBuilder()
                .expireAfterWrite(window)
                .maximumSize(100)
                .build(k -> new MessageState());
    }


    public static void main(String[] args) {
        test test = new test();

        IntStream.range(0, 10).boxed().forEach(i -> {
            new Thread(() -> test.process("aaa")).start();
            new Thread(() -> test.process("bbb")).start();

        });

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void process(String messageKey) {
        // 使用 get 替代 getIfPresent，确保原子性
        MessageState state = messageCache.get(messageKey, key -> {
            // 这个 lambda 在确保没有现有值时才会被调用
            MessageState newState = new MessageState();
            log.info("do something...");
            log.info("第一次执行业务");
            return newState;
        });
        if (!state.isDuplicate(window)) {
            log.info("do something...");
        } else {
            log.debug("Duplicate message detected: {}, count: {}",
                    messageKey, state.getCount());
        }
    }
}
