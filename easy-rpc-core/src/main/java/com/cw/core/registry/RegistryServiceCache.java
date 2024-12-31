package com.cw.core.registry;

import com.cw.core.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心本地缓存
 */
public class RegistryServiceCache {

    List<ServiceMetaInfo> serviceCache;

    public void writeCache(List<ServiceMetaInfo> newServiceCache) {
        this.serviceCache = newServiceCache;
    }

    public List<ServiceMetaInfo> readCache() {
        return this.serviceCache;
    }

    public void clearCache() {
        this.serviceCache.clear();
    }
}
