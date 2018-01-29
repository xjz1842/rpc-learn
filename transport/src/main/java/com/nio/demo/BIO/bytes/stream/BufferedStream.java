package com.nio.demo.BIO.bytes.stream;

import org.junit.Test;

import java.io.*;

public class BufferedStream {

    @Test
    public void test() throws IOException {

        //定义源文件与目标文件
        String target = this.getClass().getClassLoader().getResource("target.txt").getPath();

        System.out.println("target" + target);

        try{
            BufferedInputStream bufferedInputStream = new BufferedInputStream(this.getClass().getClassLoader().getResourceAsStream("src.txt"));
            BufferedOutputStream bufferdOutputStream = new BufferedOutputStream(new FileOutputStream(new File(target)));

            //通过缓冲输入流读取源文件内容,并写入到缓冲输出流,最终写入文件
            byte[] buff = new byte[1024];

            int byt;
            while ((byt = bufferedInputStream.read(buff, 0, buff.length)) != -1) {
                bufferdOutputStream.write(buff, 0, byt);
            }
            bufferdOutputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
