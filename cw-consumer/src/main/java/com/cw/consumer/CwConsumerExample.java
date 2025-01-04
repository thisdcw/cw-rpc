package com.cw.consumer;

import com.cw.common.model.User;
import com.cw.common.service.UserService;
import com.cw.core.bootstrap.ConsumerBootstrap;
import com.cw.core.proxy.ServiceProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author thisdcw-com
 * @date 2024/8/20 14:33
 */
public class CwConsumerExample {

    private static final Logger cLog = LoggerFactory.getLogger("consumer");

    public static void main(String[] args) {

        // 服务提供者初始化
        ConsumerBootstrap.init();

        // 获取代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("thisdcw");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }


    }
}