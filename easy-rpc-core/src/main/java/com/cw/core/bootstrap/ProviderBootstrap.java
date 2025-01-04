package com.cw.core.bootstrap;

import com.cw.core.RpcApplication;
import com.cw.core.config.RegistryConfig;
import com.cw.core.config.RpcConfig;
import com.cw.core.model.ServiceMetaInfo;
import com.cw.core.model.ServiceRegisterInfo;
import com.cw.core.registry.LocalRegistry;
import com.cw.core.registry.Registry;
import com.cw.core.registry.RegistryFactory;
import com.cw.core.server.tcp.VertxTcpServer;

import java.util.List;

public class ProviderBootstrap {

    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {

        //框架初始化
        RpcApplication.init();

        //全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        //注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            //本地注册
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            //注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getHost());
            serviceMetaInfo.setServicePort(rpcConfig.getPort());

            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + "服务注册失败", e);
            }
        }

        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getPort());
    }
}
