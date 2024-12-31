package com.cw.core.server;

/**
 * @author thisdcw-com
 * @date 2024/8/20 14:49
 */
public interface HttpServer {

    /**
     * 启动服务器
     *
     * @param port 端口号
     */
    void doStart(int port);
}
