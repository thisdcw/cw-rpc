package com.cw.consumer;

import com.cw.common.model.User;
import com.cw.common.service.UserService;
import com.cw.core.proxy.ServiceProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author thisdcw-com
 * @date 2024/8/20 14:33
 */
public class CwConsumer {

    private static final Logger cLog = LoggerFactory.getLogger("consumer");

    public static void main(String[] args) {
        // 动态代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("thisdcw");

        User newUser = userService.getUser(user);
        if (newUser != null) {
            cLog.info(newUser.getName());
        } else {
            cLog.info("user1 == null");
        }

        long number = userService.getNumber();
        cLog.info("{}", number);

        User aUser = userService.getUser(user);
        if (aUser != null) {
            cLog.info("用户名: {}", aUser.getName());
        } else {
            cLog.info("user2 == null");
        }
    }
}