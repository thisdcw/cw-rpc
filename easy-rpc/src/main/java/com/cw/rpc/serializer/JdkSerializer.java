package com.cw.rpc.serializer;

import java.io.*;

/**
 * Jdk 序列化器
 *
 * @author thisdcw-com
 * @date 2024/8/20 16:00
 */
public class JdkSerializer implements Serializer {

    /**
     * 序列化
     *
     * @param obj 要序列化的对象
     * @param <T>
     * @return
     * @throws IOException
     */
    @Override
    public <T> byte[] serialize(T obj) throws IOException {

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);

        oos.writeObject(obj);
        oos.close();

        return os.toByteArray();
    }

    /**
     * 反序列化
     *
     * @param data  要反序列化的字节数组
     * @param clazz 反序列化的对象
     * @param <T>
     * @return
     * @throws IOException
     */
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {

        ByteArrayInputStream is = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(is);

        try {
            return (T) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
