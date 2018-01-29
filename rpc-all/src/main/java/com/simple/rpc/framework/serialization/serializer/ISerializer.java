package com.simple.rpc.framework.serialization.serializer;

public interface ISerializer {

    /**
     * 序列化
     * @param obj 对象
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T obj);

    /**
     *
     * @param data 数据
     * @param clazz 序列化成的对象
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] data,Class<T> clazz);
}
