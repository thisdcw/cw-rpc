package com.cw.consumer;

import com.cw.annotation.RpcReference;
import com.cw.common.model.User;
import com.cw.common.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {

    @RpcReference
    private UserService userService;

    public void test() {
        User user = new User();
        user.setName("thisdcw");
        User user1 = userService.getUser(user);
        System.out.println(user1.getName());
    }
}
