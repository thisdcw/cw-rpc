package com.cw.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.cw.rpc.model.RpcRequest;
import com.cw.rpc.model.RpcResponse;
import com.cw.rpc.serializer.JdkSerializer;
import com.cw.rpc.serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author thisdcw-com
 * @date 2024/8/20 16:46
 */
public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Serializer serializer = new JdkSerializer();

        //构造请求
        RpcRequest rpcRequest = RpcRequest.builder().serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        try {
            //序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            //发送请求
            //todo 注意,这里地址被硬编码了(需要注册中心和服务发现机制解决)
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:2000").body(bodyBytes).execute()) {
                byte[] result = httpResponse.bodyBytes();

                //反序列化
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
