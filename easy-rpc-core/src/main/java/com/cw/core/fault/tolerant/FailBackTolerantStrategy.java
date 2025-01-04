package com.cw.core.fault.tolerant;

import com.cw.core.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 降级到其他服务
 */
@Slf4j
public class FailBackTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> params, Exception e) {
        //todo 自行扩展,获取降级服务并调用
        return null;
    }
}
