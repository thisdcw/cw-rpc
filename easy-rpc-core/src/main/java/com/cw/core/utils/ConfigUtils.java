package com.cw.core.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 配置工具类
 *
 * @author thisdcw@gmail.com
 * @date 2024/8/20 21:27
 */
@Slf4j
public class ConfigUtils {


    /**
     * 加载配置对象
     *
     * @param tClass
     * @param prefix
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix) throws IOException {
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
    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) throws IOException {
        log.info("加载配置文件");
        return loadProperties(tClass, prefix, environment);
//        return loadYaml(tClass, environment);
    }

    public static <T> T loadProperties(Class<T> tClass, String prefix, String environment) {
        StringBuilder sb = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            sb.append("-").append(environment);
        }
        sb.append(".properties");

        Props props = new Props();
        return props.toBean(tClass, prefix);
    }

    public static <T> T loadYaml(Class<T> tClass, String environment) throws IOException {
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

        StringBuilder sb = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            sb.append("-").append(environment);
        }
        sb.append(".yaml");
        String projectPath = System.getProperty("user.dir");
        String resourcePath = projectPath + "/src/main/resources/config.yaml";
        String path = ConfigUtils.class.getClass().getClassLoader().getResource("").getPath();
        log.info("资源文件夹路径: {}", resourcePath);


        InputStream inputStream = ConfigUtils.class.getClassLoader().getResourceAsStream(sb.toString());
        return yamlMapper.readValue(inputStream, tClass);
    }


}
