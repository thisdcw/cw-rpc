package com.cw.core;

import com.cw.core.config.RegistryConfig;
import com.cw.core.config.RpcConfig;
import com.cw.core.constant.RpcConstant;
import com.cw.core.registry.Registry;
import com.cw.core.registry.RegistryFactory;
import com.cw.core.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC框架应用
 *
 * @author thisdcw@gmail.com
 * @date 2024/8/20 21:36
 */
@Slf4j
public class RpcApplication {

    private static volatile RpcConfig rpcConfig;

    /**
     * 框架初始化
     *
     * @param config 配置
     */
    public static void init(RpcConfig config) {
        rpcConfig = config;
        log.info("rpc init ,config = {}", config.toString());
        //注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("register init ,config = {}", registryConfig);

        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    /**
     * 初始化
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            log.error("加载失败,使用默认配置");
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 获取配置(双检索单例模式)
     *
     * @return
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
