package com.simple.rpc.framework.serialization.serializer.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.simple.rpc.framework.serialization.serializer.ISerializer;

public class FastjsonSerializer implements ISerializer {

    static final String charsetName="UTF-8";

    @Override
    public <T> byte[] serialize(T obj) {
        SerializeWriter out =  new SerializeWriter();

        JSONSerializer serializer = new JSONSerializer(out);

        //对于枚举的特殊处理
        serializer.config(SerializerFeature.WriteEnumUsingToString,true);

        serializer.config(SerializerFeature.WriteClassName,true);
        serializer.write(obj);
        return out.toBytes(charsetName);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return JSON.parseObject(new String(data),clazz);
    }
}