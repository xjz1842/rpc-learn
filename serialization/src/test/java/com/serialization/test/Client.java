package com.serialization.test;

import com.serialization.test.bean.User;
import com.simple.rpc.serialization.Serialization;
import com.simple.rpc.serialization.impl.FastJsonSerialization;
import com.simple.rpc.serialization.impl.Hessian2Serialization;
import com.simple.rpc.serialization.impl.KryoSerialization;
import com.simple.rpc.serialization.impl.ProtostuffSerialization;
import org.junit.Test;

public class Client {

    @Test
    public void testFastJsonSerialize()throws Exception{
        User user = new User();
        user.setId(1);
        user.setName("323");
        user.setPhone("12345678901");

        Serialization serialization = new FastJsonSerialization();

        System.out.println("-------begin=----");
        long start = System.currentTimeMillis();

        for(int i=0; i <1000000;i++) {
            serialization.serialize(user);
        }
        System.out.println((System.currentTimeMillis()-start));
    }

    @Test
    public void testFastJsonDeserialize()throws Exception{
        User user = new User();
        user.setId(1);
        user.setName("323");
        user.setPhone("12345678901");

        Serialization serialization = new FastJsonSerialization();

        System.out.println("-------begin=----");
        long start = System.currentTimeMillis();
        byte[] data = serialization.serialize(user);

        for(int i=0; i < 1000000;i++) {
            serialization.deserialize(data,User.class);
        }
        System.out.println((System.currentTimeMillis()-start));
    }


    @Test
    public void testHessianSerialize()throws Exception{
        User user = new User();
        user.setId(1);
        user.setName("323");
        user.setPhone("12345678901");

        Serialization serialization = new Hessian2Serialization();

        System.out.println("-------begin=----");
        long start = System.currentTimeMillis();

        for(int i=0; i < 1000000;i++) {
            serialization.serialize(user);
        }
        System.out.println((System.currentTimeMillis()-start));
    }

    @Test
    public void testHessianDeserialize()throws Exception{
        User user = new User();
        user.setId(1);
        user.setName("323");
        user.setPhone("12345678901");

        Serialization serialization = new Hessian2Serialization();

        System.out.println("-------begin=----");
        long start = System.currentTimeMillis();
        byte[] data = serialization.serialize(user);

        for(int i=0; i < 1000000;i++) {
            serialization.deserialize(data,User.class);
        }
        System.out.println((System.currentTimeMillis()-start));
    }


    @Test
    public void testKyroSerialize()throws Exception{
        User user = new User();
        user.setId(1);
        user.setName("323");
        user.setPhone("12345678901");

        Serialization serialization = new KryoSerialization();

        System.out.println("-------begin=----");
        long start = System.currentTimeMillis();

        for(int i=0; i < 1000000;i++) {
            serialization.serialize(user);
        }
        System.out.println((System.currentTimeMillis()-start));
    }


    @Test
    public void testKyroDeserialize()throws Exception{
        User user = new User();
        user.setId(1);
        user.setName("323");
        user.setPhone("12345678901");

        Serialization serialization = new KryoSerialization();

        System.out.println("-------begin=----");
        long start = System.currentTimeMillis();
        byte[] data = serialization.serialize(user);

        for(int i=0; i < 1000000;i++) {
            serialization.deserialize(data,User.class);
        }
        System.out.println((System.currentTimeMillis()-start));
    }


    @Test
    public void testProtostuffSerialize()throws Exception{
        User user = new User();
        user.setId(1);
        user.setName("323");
        user.setPhone("12345678901");

        Serialization serialization = new ProtostuffSerialization();

        System.out.println("-------begin=----");
        long start = System.currentTimeMillis();

        for(int i=0; i < 1000000;i++) {
            serialization.serialize(user);
        }
        System.out.println((System.currentTimeMillis()-start));
    }


    @Test
    public void testProtostuffDeserialize()throws Exception{
        User user = new User();
        user.setId(1);
        user.setName("323");
        user.setPhone("12345678901");

        Serialization serialization = new ProtostuffSerialization();

        System.out.println("-------begin=----");
        long start = System.currentTimeMillis();
        byte[] data = serialization.serialize(user);

        for(int i=0; i < 1000000;i++) {
            serialization.deserialize(data,User.class);
        }
        System.out.println((System.currentTimeMillis()-start));
    }

}
