package com.cw.core.loadbalancer;

import com.cw.core.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希负载均衡器
 */
public class ConsistenHashLoadBalancer implements LoadBalancer {

    /**
     * 一致性Hash环,存放虚拟节点
     */
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * 虚拟节点数
     */
    public static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }
        //构建虚拟节点环
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }
        //获取调用请求的Hash值
        int hash = getHash(requestParams);

        //选择最接近且大于等于调用请求Hash值的虚拟节点
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry == null) {
            //如果没有大于等于调用请求Hash值的虚拟节点,返回环首部节点
            entry = virtualNodes.firstEntry();
        }

        return entry.getValue();
    }


    /**
     * hash算法
     *
     * @param key
     * @return
     */
    private int getHash(Object key) {
        return key.hashCode() & 0x7fffffff;
    }
}
