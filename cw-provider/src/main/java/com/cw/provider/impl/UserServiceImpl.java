package com.cw.provider.impl;

import com.cw.common.model.User;
import com.cw.common.service.UserService;

/**
 * @author thisdcw-com
 * @date 2024/8/20 14:41
 */
public class UserServiceImpl implements UserService {

    @Override
    public User getUser(User user) {
        System.out.println("用户名: " + user.getName());
        return user;
    }
}
