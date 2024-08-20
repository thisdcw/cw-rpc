package com.cw.consumer;

import com.cw.common.model.User;
import com.cw.common.service.UserService;
import com.cw.rpc.config.RpcConfig;
import com.cw.rpc.proxy.ServiceProxyFactory;
import com.cw.rpc.utils.ConfigUtils;

/**
 * @author thisdcw-com
 * @date 2024/8/20 14:33
 */
public class CwConsumer {
    public static void main(String[] args) {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);
        // 动态代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("thisdcw");

        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}