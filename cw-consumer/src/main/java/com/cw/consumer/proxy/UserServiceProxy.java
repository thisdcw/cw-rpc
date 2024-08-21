package com.cw.consumer.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.cw.common.model.User;
import com.cw.common.service.UserService;
import com.cw.rpc.RpcApplication;
import com.cw.rpc.model.RpcRequest;
import com.cw.rpc.model.RpcResponse;
import com.cw.rpc.serializer.JdkSerializer;
import com.cw.rpc.serializer.Serializer;

import java.io.IOException;

/**
 * 静态代理
 *
 * @author thisdcw-com
 * @date 2024/8/20 16:36
 */
public class UserServiceProxy implements UserService {

    @Override
    public User getUser(User user) {

        //指定序列化器
        Serializer serializer = new JdkSerializer();

        //发请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();

        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            try (HttpResponse httpResponse = HttpRequest.post("http://" + RpcApplication.getRpcConfig().getHost() + ":" + RpcApplication.getRpcConfig().getPort()).body(bodyBytes).execute()) {
                result = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
