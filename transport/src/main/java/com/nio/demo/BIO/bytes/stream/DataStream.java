package com.nio.demo.BIO.bytes.stream;

import org.junit.Test;

import java.io.*;

public class DataStream {

    @Test
    public void test() throws IOException{
        //将java原生类型数据通过DataOutputStream写入文件
        FileOutputStream outputStream = new FileOutputStream(this.getClass().getClassLoader().getResource("target.txt").getPath());
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        dataOutputStream.writeInt(2018);
        dataOutputStream.writeUTF("你好,java Blocking I/O!");
        dataOutputStream.writeBoolean(true);

        dataOutputStream.flush();

        outputStream.close();
        outputStream.close();

        //使用DataInputStream从文件中按照写入顺序读取java原生类型数据
        InputStream fin = this.getClass().getClassLoader().getResourceAsStream("target.txt");

        DataInputStream dis = new DataInputStream(fin);

        System.out.println(dis.readInt());
        System.out.println(dis.readUTF());
        System.out.println(dis.readBoolean());

        dis.close();
        fin.close();
    }
};

