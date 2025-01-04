package com.cw.core.loadbalancer;

import com.cw.core.spi.SpiLoader;

/**
 * 负载均衡器工厂(工厂模式,用于获取负载均衡器对象)
 */
public class LoadBalancerFactory {

    static {
        SpiLoader.load(LoadBalancer.class);
    }

    /**
     * 默认负载均衡器
     */
    public static final LoadBalancer DEFAULT_LOAD_BALANCER = new RoundRobinLoadBalancer();

    /**
     * 获取实例
     *
     * @return
     */
    public static LoadBalancer getInstance(String key) {
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }
}
