package com.cw.core.fault.tolerant;

import com.cw.core.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 转移到其他服务节点并调用
 */
@Slf4j
public class FailOverTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> params, Exception e) {
        //todo 自行扩展,获取其他服务节点并调用
        return null;
    }
}
