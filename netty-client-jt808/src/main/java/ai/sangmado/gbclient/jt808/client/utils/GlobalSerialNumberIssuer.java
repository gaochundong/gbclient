package ai.sangmado.gbclient.jt808.client.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 全局流水号发号器
 */
public class GlobalSerialNumberIssuer {
    private static AtomicInteger counter = new AtomicInteger(1);

    /**
     * 获取当前值
     *
     * @return 当前值
     */
    public static int current() {
        return counter.get();
    }

    /**
     * 获取下一个新值
     *
     * @return 下一个新值
     */
    public static int next() {
        return counter.incrementAndGet();
    }

    /**
     * 增加偏移量，再获取下一个新值
     *
     * @return 下一个新值
     */
    public static int next(int delta) {
        return counter.addAndGet(delta);
    }
}
