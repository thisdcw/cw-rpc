package com.cw.core.fault.retry;

import com.cw.core.spi.SpiLoader;
import lombok.extern.slf4j.Slf4j;

/**
 * 重试策略工厂(用于获取重试器对象)
 */
@Slf4j
public class RetryStrategyFactory {

    static {
        SpiLoader.load(RetryStrategy.class);
    }

    /**
     * 默认重试策略
     */
    public static final RetryStrategy DEFAULT_STRATEGY = new NoRetryStrategy();


    public static RetryStrategy getInstance(String key) {
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }
}
