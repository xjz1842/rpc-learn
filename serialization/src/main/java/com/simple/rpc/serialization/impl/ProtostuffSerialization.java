package com.simple.rpc.serialization.impl;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.simple.rpc.serialization.Serialization;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.io.IOException;

public class ProtostuffSerialization implements Serialization {

    private Objenesis objenesis = new ObjenesisStd();

    @Override
    public byte[] serialize(Object object) throws IOException {

        Class<?> clazz = object.getClass();

        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

        try {
            Schema schema = RuntimeSchema.createFrom(clazz);
            return ProtostuffIOUtil.toByteArray(object, schema, buffer);
        } catch (Exception e) {
            throw e;
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {

        T message = objenesis.newInstance(clazz);

        Schema<T> schema = RuntimeSchema.createFrom(clazz);

        ProtostuffIOUtil.mergeFrom(bytes, message, schema);

        return message;
    }
}
