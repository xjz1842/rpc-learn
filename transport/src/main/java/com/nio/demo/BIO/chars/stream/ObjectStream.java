package com.nio.demo.BIO.chars.stream;

import org.junit.Test;

import java.io.*;

public class ObjectStream {

    /**
     * 对象流
     * @throws IOException
     */
     @Test
     public void testObjectStream()throws IOException{

         //创建文件字节输入流
         InputStream inputStream = new FileInputStream("/Users/zxj/github/rpc-learn/transport/src/main/resources/src.txt");

         //利用桥梁InputStreamReader将文件字节输入流inputStream转换成字符输入流inputStreamReader
         InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

         //利用BufferedReader包装字符输入流inputStreamReader提高性能
         BufferedReader  bufferedReader = new BufferedReader(inputStreamReader);

         //创建文件字节输出流
         FileOutputStream outputStream = new FileOutputStream("/Users/zxj/github/rpc-learn/transport/src/main/resources/target.txt");

         //利用桥梁outputStreamWriter将文件字节输出流outputStream转换成字符输出流outputStreamWriter
         OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

         //利用BufferedWriter包装字符输出流inputStreamReader提高性能
         BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

         //将文件src.txt文本内容写入到target.txt

         String line = null;
         while ((line = bufferedReader.readLine()) != null) {
             bufferedWriter.write(line);
             bufferedWriter.newLine();
         }

         bufferedReader.close();
         bufferedWriter.close();
         inputStream.close();
         outputStream.close();
     }

}
