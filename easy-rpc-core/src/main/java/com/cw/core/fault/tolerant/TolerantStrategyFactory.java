package com.cw.core.fault.tolerant;

import com.cw.core.spi.SpiLoader;
import lombok.extern.slf4j.Slf4j;

/**
 * 容错策略工厂(用于获取重试器对象)
 */
@Slf4j
public class TolerantStrategyFactory {

    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    /**
     * 默认容错策略
     */
    public static final TolerantStrategy DEFAULT_STRATEGY = new FailFastTolerantStrategy();


    public static TolerantStrategy getInstance(String key) {
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }
}
