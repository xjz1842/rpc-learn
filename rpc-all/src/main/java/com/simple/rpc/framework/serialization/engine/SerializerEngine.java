package com.simple.rpc.framework.serialization.engine;


import com.simple.rpc.framework.serialization.common.SerializeType;
import com.simple.rpc.framework.serialization.serializer.ISerializer;
import com.simple.rpc.framework.serialization.serializer.impl.FastjsonSerializer;
import com.simple.rpc.framework.serialization.serializer.impl.HessianSerializer;
import com.simple.rpc.framework.serialization.serializer.impl.ProtoStuffSerializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SerializerEngine {

    public static final Map<SerializeType, ISerializer> serializerMap = new ConcurrentHashMap<>();

    static {
//        serializerMap.put(SerializeType.DefaultJavaSerializer, new DefaultJavaSerializer());
        serializerMap.put(SerializeType.HessianSerializer, new HessianSerializer());
//        serializerMap.put(SerializeType.JSONSerializer, new JSONSerializer());
//        serializerMap.put(SerializeType.XmlSerializer, new XmlSerializer());
          serializerMap.put(SerializeType.FastjsonSerializer,new FastjsonSerializer());
         serializerMap.put(SerializeType.ProtoStuffSerializer, new ProtoStuffSerializer());

//        serializerMap.put(SerializeType.MarshallingSerializer, new MarshallingSerializer());

         //以下三类不能使用普通的java bean
//        serializerMap.put(SerializeType.AvroSerializer, new AvroSerializer());
//        serializerMap.put(SerializeType.ThriftSerializer, new ThriftSerializer());
//        serializerMap.put(SerializeType.ProtocolBufferSerializer, new ProtocolBufferSerializer());
    }


    public static <T> byte[] serialize(T obj, String serializeType) {
        SerializeType serialize = SerializeType.queryByType(serializeType);
        if (serialize == null) {
            throw new RuntimeException("serialize is null");
        }

        ISerializer serializer = serializerMap.get(serialize);
        if (serializer == null) {
            throw new RuntimeException("serialize error");
        }

        try {
            return serializer.serialize(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static <T> T deserialize(byte[] data, Class<T> clazz, String serializeType) {

        SerializeType serialize = SerializeType.queryByType(serializeType);
        if (serialize == null) {
            throw new RuntimeException("serialize is null");
        }
        ISerializer serializer = serializerMap.get(serialize);
        if (serializer == null) {
            throw new RuntimeException("serialize error");
        }

        try {
            return serializer.deserialize(data, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
