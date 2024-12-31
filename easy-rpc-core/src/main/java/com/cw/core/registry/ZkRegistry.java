package com.cw.core.registry;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.cw.core.model.ServiceMetaInfo;
import com.cw.core.config.RegistryConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ZkRegistry implements Registry {

    private static final Logger rpcLog = LoggerFactory.getLogger("rpcLog");

    private CuratorFramework client;

    private ServiceDiscovery<ServiceMetaInfo> serviceDiscovery;

    /**
     * 本机注册的节点key集合(用于维护续期)
     */
    public final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * 注册中心服务缓存
     */
    public final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 正在监听的key集合
     */
    public final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    /**
     * 根节点
     */
    private static final String ZK_ROOT_PATH = "/rpc/zk";

    @Override
    public void init(RegistryConfig registryConfig) {
        rpcLog.info("初始化zookeeper配置: {}", registryConfig.toString());
        String address = registryConfig.getAddress();
        client = CuratorFrameworkFactory
                .builder()
                .connectString(address)
                .retryPolicy(new ExponentialBackoffRetry(Math.toIntExact(registryConfig.getTimeout()), 3))
                .build();

        serviceDiscovery = ServiceDiscoveryBuilder
                .builder(ServiceMetaInfo.class)
                .client(client)
                .basePath(ZK_ROOT_PATH)
                .serializer(new JsonInstanceSerializer<>(ServiceMetaInfo.class))
                .build();

        try {
            client.start();
            serviceDiscovery.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        rpcLog.info("注册: {}", serviceMetaInfo.toString());
        serviceDiscovery.registerService(buildServiceInstance(serviceMetaInfo));

        String registryKey = ZK_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();
        localRegisterNodeKeySet.add(registryKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        rpcLog.info("取消注册: {}", serviceMetaInfo.toString());
        try {
            serviceDiscovery.unregisterService(buildServiceInstance(serviceMetaInfo));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String registryKey = ZK_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();
        localRegisterNodeKeySet.remove(registryKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        rpcLog.info("服务发现: {}", serviceKey);
        List<ServiceMetaInfo> serviceMetaInfos = registryServiceCache.readCache();
        if (serviceMetaInfos != null) {
            rpcLog.info("查询缓存");
            return serviceMetaInfos;
        }
        rpcLog.info("查询注册中心");
        try {
            Collection<ServiceInstance<ServiceMetaInfo>> serviceInstances = serviceDiscovery.queryForInstances(serviceKey);
            List<ServiceMetaInfo> serviceMetaInfoList = serviceInstances.stream().map(ServiceInstance::getPayload).collect(Collectors.toList());

            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    @Override
    public void destroy() {
        rpcLog.info("节点下线");
        for (String s : localRegisterNodeKeySet) {
            try {
                client.delete().guaranteed().forPath(s);
            } catch (Exception e) {
                throw new RuntimeException(s + "节点下线失败");
            }
        }

        if (client != null) {
            client.close();
        }
    }

    @Override
    public void heartBeat() {

    }

    @Override
    public void watch(String serviceKey) {
        String watchKey = ZK_ROOT_PATH + "/" + serviceKey;
        boolean add = watchingKeySet.add(watchKey);
        if (add) {
            CuratorCache curatorCache = CuratorCache.build(client, watchKey);
            curatorCache.start();
            curatorCache.listenable().addListener(CuratorCacheListener.builder().forDeletes(childData -> registryServiceCache.clearCache()).forChanges((oldNode, node) -> registryServiceCache.clearCache()).build());
        }
    }

    private ServiceInstance<ServiceMetaInfo> buildServiceInstance(ServiceMetaInfo serviceMetaInfo) {
        String serviceAddress = serviceMetaInfo.getServiceHost() + ":" + serviceMetaInfo.getServicePort();

        try {
            return ServiceInstance.<ServiceMetaInfo>builder().id(serviceAddress).name(serviceMetaInfo.getServiceKey()).address(serviceAddress).payload(serviceMetaInfo).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
