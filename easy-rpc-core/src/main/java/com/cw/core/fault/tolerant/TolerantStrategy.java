package com.cw.core.fault.tolerant;

import com.cw.core.model.RpcRequest;
import com.cw.core.model.RpcResponse;

import java.util.Map;

/**
 * 容错策略
 */
public interface TolerantStrategy {

    /**
     * 容错
     *
     * @param params 上下文
     * @param e      异常
     * @return
     */
    RpcResponse doTolerant(Map<String, Object> params, Exception e);
}
