package com.cw.core.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.cw.core.config.RegistryConfig;
import com.cw.core.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EtcdRegistry implements Registry {

    private static final Logger rpcLog = LoggerFactory.getLogger("rpcLog");

    private Client client;

    private KV kvClient;

    public final Set<String> localRegisterNodeKeySet = new HashSet<>();

    public final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    public final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    private static final String ETCD_ROOT_PATH = "/rpc/";

    @Override
    public void init(RegistryConfig registryConfig) {
        rpcLog.info("初始化Etcd配置: {}", registryConfig.toString());
        String address = registryConfig.getAddress();
        client = Client.builder().endpoints(address).connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        kvClient = client.getKVClient();
        heartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        rpcLog.info("注册: {}", serviceMetaInfo.toString());
        //创建lease和KV客户端
        Lease leaseClient = client.getLeaseClient();
        //创建一个30秒的租约
        long id = leaseClient.grant(30).get().getID();
        //设置要存储的键值对
        String registry = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registry, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        //将键值对与租约关联起来,并设置过期时间
        PutOption build = PutOption.builder().withLeaseId(id).build();
        kvClient.put(key, value, build).get();

        localRegisterNodeKeySet.add(registry);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        rpcLog.info("取消注册: {}", serviceMetaInfo.toString());
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(registryKey, StandardCharsets.UTF_8));
        localRegisterNodeKeySet.remove(registryKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        rpcLog.info("服务发现: {}", serviceKey);
        // 优先从缓存获取服务
        // 原教程代码，不支持多个服务同时缓存
         List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache();
        if (cachedServiceMetaInfoList != null) {
            return cachedServiceMetaInfoList;
        }

        // 前缀搜索，结尾一定要加 '/'
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";

        try {
            // 前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(
                            ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                            getOption)
                    .get()
                    .getKvs();
            // 解析服务信息
            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream()
                    .map(keyValue -> {
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        // 监听 key 的变化
                        watch(key);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());
            // 写入服务缓存
             registryServiceCache.writeCache(serviceMetaInfoList);
            // 优化后的代码，支持多个服务同时缓存
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
                kvClient.delete(ByteSequence.from(s, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException("节点下线失败" + e);
            }
        }
        //释放资源
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void heartBeat() {
        //十秒续签一次
        CronUtil.schedule("*/10 * * * * *", (Task) () -> {
            for (String key : localRegisterNodeKeySet) {
                try {
                    List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8)).get().getKvs();
                    //该节点已过期,需要重启节点才能重新注册
                    if (CollUtil.isEmpty(keyValues)) {
                        continue;
                    }
                    //节点未过期,重新注册
                    KeyValue keyValue = keyValues.get(0);
                    String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                    ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                    register(serviceMetaInfo);

                } catch (Exception e) {
                    throw new RuntimeException(key + " 续签失败 ", e);
                }
            }
        });

        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceKey) {

        Watch watchClient = client.getWatchClient();

        boolean add = watchingKeySet.add(serviceKey);
        if (add) {
            watchClient.watch(ByteSequence.from(serviceKey, StandardCharsets.UTF_8), watchResponse -> {
                for (WatchEvent event : watchResponse.getEvents()) {
                    switch (event.getEventType()) {
                        //key删除时触发
                        case DELETE:
                            //清理缓存
                            registryServiceCache.clearCache();
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }

            });
        }
    }
}
