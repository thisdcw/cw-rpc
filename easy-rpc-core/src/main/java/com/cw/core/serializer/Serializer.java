package com.cw.core.serializer;

import java.io.IOException;

/**
 * 序列化器接口
 *
 * @author thisdcw-com
 * @date 2024/8/20 15:57
 */
public interface Serializer {

    /**
     * 序列化
     *
     * @param obj 要序列化的对象
     * @param <T> 要序列化的对象的类型
     * @return 字节数组
     * @throws IOException IO异常
     */
    <T> byte[] serialize(T obj) throws IOException;

    /**
     * 反序列化
     *
     * @param data  要反序列化的字节数组
     * @param clazz 反序列化的对象
     * @param <T>   反序列化的对象的类型
     * @return 对象
     * @throws IOException IO异常
     */
    <T> T deserialize(byte[] data, Class<T> clazz) throws IOException;
}
