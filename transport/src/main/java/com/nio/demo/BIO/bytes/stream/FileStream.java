package com.nio.demo.BIO.bytes.stream;

import org.junit.Test;

import java.io.*;

public class FileStream {

    @Test
    public void testFileStream()throws IOException{
        InputStream inputStream = null;
        FileOutputStream outputStream =  new FileOutputStream(this.getClass().getClassLoader().getResource("target.txt").getPath());

        //定义源文件与目标文件

        String srcText = "src.txt";

        try{
            //实例化文件输入流与文件输出流
            inputStream = this.getClass().getClassLoader().getResourceAsStream(srcText);

            //通过文件输入流读取源文件内容,并写入到目标文件
            int byt;

            while((byt = inputStream.read()) != -1){
                outputStream.write(byt);
            }

        }finally {
            if(inputStream != null) {
                inputStream.close();
            }
            if(outputStream != null) {
                outputStream.close();
            }
        }

    }


}
