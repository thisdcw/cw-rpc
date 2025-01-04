package com.cw.core.config;

import com.cw.core.registry.RegistryKeys;
import lombok.Data;

@Data
public class RegistryConfig {

    /**
     * 注册中心类别
     */
    private String registry = RegistryKeys.REDIS;

    /**
     * 注册中心地址
     */
//    private String address = "http://120.79.4.139:2379";
    private String address = "120.79.4.139:6379";

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password = "this201314";

    /**
     * 超时时间
     */
    private Long timeout = 10000L;
}
