package com.nio.demo.client;

import com.nio.demo.common.User;
import org.junit.Test;

import java.io.*;

public class StreamsTest {

    /**
     * 字节流以及对象流的使用
     * @throws Exception
     */
    @Test
    public void testByteArray() throws Exception{

        User user = new User();
        user.setName("lisi");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

        objectOutputStream.writeObject(user);
        objectOutputStream.close();

        byte[] bytes =  byteArrayOutputStream.toByteArray();

        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);

        ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);

        User u  =  (User)objectInputStream.readObject();

        System.out.println(u);
    }

    /**
     * 测试对象流
     */
    @Test
    public void testObjectStream()throws Exception{
        User user = new User();
        user.setName("lisi");

        FileOutputStream outputStream =  new FileOutputStream(this.getClass().getClassLoader().getResource("target.txt").getPath());

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        objectOutputStream.writeObject(user);
        objectOutputStream.close();

        ObjectInputStream objectInputStream = new ObjectInputStream(this.getClass().getClassLoader().getResourceAsStream("target.txt"));

        User u  =  (User)objectInputStream.readObject();

        System.out.println(u);



    }



}
