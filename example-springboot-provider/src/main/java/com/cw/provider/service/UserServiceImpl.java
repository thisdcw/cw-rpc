package com.cw.provider.service;

import com.cw.common.model.User;
import com.cw.common.service.UserService;
import org.springframework.stereotype.Service;
import com.cw.annotation.RpcService;

/**
 * 用户服务实现类
 */
@RpcService
@Service
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名: " + user.getName());
        return user;
    }

    @Override
    public short getNumber() {
        return 22;
    }
}
