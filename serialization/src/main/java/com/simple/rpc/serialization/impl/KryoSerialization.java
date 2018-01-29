package com.simple.rpc.serialization.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.simple.rpc.serialization.Serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoSerialization implements Serialization{

    @Override
    public byte[] serialize(Object object) throws IOException {

        Kryo kryo = kyroLocal.get();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        Output output = new Output(byteArrayOutputStream);

        kryo.writeObject(output,object);

        output.close();

        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
       Kryo  kryo  =  kyroLocal.get();

       ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        Input input = new Input(byteArrayInputStream);

        input.close();

        return kryo.readObject(input,clazz);
    }

    private static final ThreadLocal<Kryo> kyroLocal = new ThreadLocal<Kryo>(){
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.setReferences(true);
            kryo.setRegistrationRequired(false);
            return kryo;
        }
    };
}
