package com.simple.rpc.serialization.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.simple.rpc.serialization.Serialization;

import java.io.IOException;

public class FastJsonSerialization implements Serialization{

    static final String charsetName="UTF-8";

    @Override
    public byte[] serialize(Object object) throws IOException {

        SerializeWriter out =  new SerializeWriter();

        JSONSerializer serializer = new JSONSerializer(out);

        //对于枚举的特殊处理
        serializer.config(SerializerFeature.WriteEnumUsingToString,true);
        serializer.config(SerializerFeature.WriteClassName,true);

        serializer.write(object);

        return out.toBytes(charsetName);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        return JSON.parseObject(new String(bytes),clazz);
    }

}
