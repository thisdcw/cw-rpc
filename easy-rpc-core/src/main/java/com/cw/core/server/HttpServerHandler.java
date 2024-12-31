package com.cw.core.server;

import com.cw.core.RpcApplication;
import com.cw.core.model.RpcRequest;
import com.cw.core.registry.LocalRegistry;
import com.cw.core.serializer.SerializerFactory;
import com.cw.core.model.RpcResponse;
import com.cw.core.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Http 请求处理
 *
 * @author thisdcw-com
 * @date 2024/8/20 16:13
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {

    private static final Logger rLog = LoggerFactory.getLogger("rpcLog");

    @Override
    public void handle(HttpServerRequest request) {

        //指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        //记录日志
        rLog.info("Request received: {} {}", request.method(), request.uri());
        System.out.println();

        //异步处理HTTP请求
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //构造响应结果对象
            RpcResponse rpcResponse = new RpcResponse();

            //如果请求为null,直接返回
            if (rpcRequest == null) {
                rpcResponse.setMessage("rpc request is null");
                doResponse(request, rpcResponse, serializer);
                return;
            }

            try {
                //获取要调用的服务实现类,通过反射调用
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());

                //封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            //响应
            doResponse(request, rpcResponse, serializer);
        });

    }

    /**
     * 响应
     *
     * @param request
     * @param rpcResponse
     * @param serializer
     */
    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {

        HttpServerResponse response = request.response().putHeader("Content-Type", "application/json");

        try {
            byte[] serialized = serializer.serialize(rpcResponse);
            response.end(Buffer.buffer(serialized));
        } catch (Exception e) {
            e.printStackTrace();
            response.end(Buffer.buffer());
        }
    }
}
