package com.cw.core.registry;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.cw.core.config.RegistryConfig;
import com.cw.core.model.ServiceMetaInfo;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RedisRegistry implements Registry {

    private static final Logger rpcLog = LoggerFactory.getLogger("rpcLog");

    private RedisClient client;

    private StatefulRedisConnection<String, String> connection;

    private RedisCommands<String, String> commands;

    public final Set<String> localRegisterNodeKeySet = new HashSet<>();

    @Deprecated
    public final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    public final RegistryServiceMultiCache registryServiceMultiCache = new RegistryServiceMultiCache();

    public final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    private static final String REDIS_ROOT_PATH = "/rpc/redis";

    @Override
    public void init(RegistryConfig registryConfig) {
        rpcLog.info("初始化Redis配置: {}", registryConfig.toString());
        String address = registryConfig.getAddress();
        client = RedisClient.create("redis://" + registryConfig.getPassword() + "@" + address);
        connection = client.connect();
        commands = connection.sync();
        heartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        rpcLog.info("注册: {}", serviceMetaInfo.toString());

        //设置要存储的键值对
        String registry = REDIS_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();

        commands.setex(registry, 30L, JSONUtil.toJsonStr(serviceMetaInfo));
        localRegisterNodeKeySet.add(registry);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        rpcLog.info("取消注册: {}", serviceMetaInfo.toString());
        String registryKey = REDIS_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();
        commands.del(registryKey);
        localRegisterNodeKeySet.remove(registryKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        rpcLog.info("服务发现: {}", serviceKey);
        // 优先从缓存获取服务
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceMultiCache.readCache(serviceKey);
        if (cachedServiceMetaInfoList != null) {
            return cachedServiceMetaInfoList;
        }
        try {
            List<String> keys1 = commands.keys("*");
            System.out.println(keys1.toString());
            List<String> keys = commands.keys(REDIS_ROOT_PATH + "/" + serviceKey + "*");
            System.out.println(keys.toString());
            // 解析服务信息
            List<ServiceMetaInfo> serviceMetaInfoList = keys.stream()
                    .map(commands::get)
                    .map(item -> JSONUtil.toBean(item, ServiceMetaInfo.class))
                    .collect(Collectors.toList());
            // 写入服务缓存
            registryServiceMultiCache.writeCache(serviceKey, serviceMetaInfoList);
            // 优化后的代码，支持多个服务同时缓存
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    @Override
    public void destroy() {
        rpcLog.info("节点下线");
        if (connection != null) {
            connection.close();
        }
        if (client != null) {
            client.shutdown();
        }
    }

    @Override
    public void heartBeat() {
        //十秒续签一次
        CronUtil.schedule("*/10 * * * * *", (Task) () -> {
            for (String key : localRegisterNodeKeySet) {
                String value = commands.get(key);
                if (value != null) {
                    commands.setex(key, 30L, value);
                }
            }
        });

        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceKey) {

    }
}
