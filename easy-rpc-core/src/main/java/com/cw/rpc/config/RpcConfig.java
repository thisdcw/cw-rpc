package com.cw.rpc.config;

import com.cw.rpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * Rpc框架配置
 *
 * @author thisdcw@gmail.com
 * @date 2024/8/20 21:24
 */
@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name = "cw-rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String host = "localhost";

    /**
     * 服务器端口号
     */
    private Integer port = 2000;

    /**
     * 模拟调用
     */
    private boolean mock = false;

    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;
}
