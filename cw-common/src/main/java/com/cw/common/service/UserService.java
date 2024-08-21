package com.cw.common.service;

import com.cw.common.model.User;

/**
 * @author thisdcw-com
 * @date 2024/8/20 14:38
 */
public interface UserService {

    /**
     * 获取用户
     *
     * @param user 用户
     * @return 用户信息
     */
    User getUser(User user);


    /**
     * 获取数字
     *
     * @return
     */
    default short getNumber() {
        return 1;
    }
}
