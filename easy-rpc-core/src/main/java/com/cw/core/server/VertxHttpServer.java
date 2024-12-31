package com.cw.core.server;

import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author thisdcw-com
 * @date 2024/8/20 14:50
 */
public class VertxHttpServer implements HttpServer {

    private static final Logger rLog = LoggerFactory.getLogger("rpcLog");

    @Override
    public void doStart(int port) {

        //创建Vertx实例
        Vertx vertx = Vertx.vertx();

        //创建HTTP服务器
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();
        //监听端口并处理请求
        server.requestHandler(new HttpServerHandler());

        //启动HTTP服务器并监听指定端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                rLog.info("Server started on port {}", port);
            } else {
                rLog.error("Server start failed on port {}", port);
            }
        });
    }
}
