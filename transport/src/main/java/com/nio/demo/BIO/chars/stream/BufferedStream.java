package com.nio.demo.BIO.chars.stream;

import org.junit.Test;

import java.io.*;

public class BufferedStream {

    /**
     * 获取字符流
     */
    @Test
    public void  testBufferStream() throws IOException {

        FileReader fr = new FileReader("/Users/zxj/github/rpc-learn/transport/src/main/resources/src.txt");
        FileWriter fw = new FileWriter("/Users/zxj/github/rpc-learn/transport/src/main/resources/target.txt");

        BufferedReader bufReader = new BufferedReader(fr);
        BufferedWriter bufWriter = new BufferedWriter(fw);

        //利用BufferedReader/BufferedWriter实现逐行读写,提高I/O性能
        String line = null;
        while ((line = bufReader.readLine()) != null) {
            bufWriter.write(line);
            bufWriter.newLine();
        }
        bufWriter.flush();
        bufReader.close();
        bufWriter.close();

    }


}
