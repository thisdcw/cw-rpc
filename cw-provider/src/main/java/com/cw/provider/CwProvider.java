package com.cw.provider;

import com.cw.common.service.UserService;
import com.cw.provider.impl.UserServiceImpl;
import com.cw.rpc.RpcApplication;
import com.cw.rpc.registry.LocalRegistry;
import com.cw.rpc.server.HttpServer;
import com.cw.rpc.server.VertxHttpServer;

/**
 * @author thisdcw-com
 * @date 2024/8/20 14:34
 */
public class CwProvider {
    public static void main(String[] args) {

        RpcApplication.init();

        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        //提供服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getPort());
    }
}