package com.cw.rpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/**
 * 配置工具类
 *
 * @author thisdcw@gmail.com
 * @date 2024/8/20 21:27
 */
public class ConfigUtils {

    /**
     * 加载配置对象
     *
     * @param tClass
     * @param prefix
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }

    /**
     * 加载配置对象,支持区分环境
     *
     * @param tClass
     * @param prefix
     * @param environment
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        StringBuilder sb = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            sb.append("-").append(environment);
        }
        sb.append(".properties");

        Props props = new Props(sb.toString());
        return props.toBean(tClass, prefix);
    }
}
