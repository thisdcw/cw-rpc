package com.cw.provider;

import com.cw.common.service.UserService;
import com.cw.provider.impl.UserServiceImpl;
import com.cw.rpc.RpcApplication;
import com.cw.rpc.config.RegistryConfig;
import com.cw.rpc.config.RpcConfig;
import com.cw.rpc.model.ServiceMetaInfo;
import com.cw.rpc.registry.LocalRegistry;
import com.cw.rpc.registry.Registry;
import com.cw.rpc.registry.RegistryFactory;
import com.cw.rpc.server.HttpServer;
import com.cw.rpc.server.VertxHttpServer;
import com.cw.rpc.spi.SpiLoader;

/**
 * @author thisdcw-com
 * @date 2024/8/20 14:34
 */
public class CwProvider {
    public static void main(String[] args) {

        RpcApplication.init();
        SpiLoader.loadAll();
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo info = new ServiceMetaInfo();
        info.setServiceName(serviceName);
        info.setServiceHost(rpcConfig.getHost());
        info.setServicePort(rpcConfig.getPort());

        try {
            registry.register(info);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //提供服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getPort());
    }
}