package com.cw.core.config;

import com.cw.core.fault.retry.RetryStrategyKeys;
import com.cw.core.fault.tolerant.TolerantStrategyKeys;
import com.cw.core.loadbalancer.LoadBalancer;
import com.cw.core.loadbalancer.LoadBalancerKeys;
import com.cw.core.loadbalancer.RoundRobinLoadBalancer;
import com.cw.core.serializer.SerializerKeys;
import lombok.Data;

/**
 * Rpc框架配置
 *
 * @author thisdcw@gmail.com
 * @date 2024/8/20 21:24
 */
@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name = "cw-rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String host = "localhost";

    /**
     * 服务器端口号
     */
    private Integer port = 2000;

    /**
     * 模拟调用
     */
    private boolean mock = false;

    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();

    /**
     * 负载均衡器
     */
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     */
    private String retryStrategy = RetryStrategyKeys.NO;

    /**
     * 容错策略
     */
    private String tolerantStrategy = TolerantStrategyKeys.FAIL_FAST;
}
