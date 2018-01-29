package com.nio.demo.nio.jdk.block;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class EchoClient {

    public static void main(String[] args) throws Exception{

        byte[] bytes = "你好,java Blocking I/O !".getBytes();
        ByteBuffer helloBuffer = ByteBuffer.wrap(bytes);
        CharBuffer charBuffer;
        Charset charset = Charset.defaultCharset();
        CharsetDecoder decoder = charset.newDecoder();

        try {
            //创建客户端socketChannel
            SocketChannel socketChannel =  SocketChannel.open();

            //如果客户端socketChannel创建成功
            if(socketChannel.isOpen()){
                socketChannel.configureBlocking(true);

                //设置网络传输参数
                socketChannel.setOption(StandardSocketOptions.SO_RCVBUF,128*1024);
                socketChannel.setOption(StandardSocketOptions.SO_SNDBUF,128*1024);
                socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE,true);
                socketChannel.setOption(StandardSocketOptions.SO_LINGER,5);

                socketChannel.connect(new InetSocketAddress("127.0.0.1",8082));

                if(socketChannel.isConnected()){
                    //向服务器附送数据
                    socketChannel.write(helloBuffer);

                    //创建接受服务器的返回数据byteBuffer
                    ByteBuffer buffer = ByteBuffer.allocate(1024);

                    while (socketChannel.read(buffer) != -1){
                        buffer.flip();
                        charBuffer = decoder.decode(buffer);
                        System.out.println(charBuffer.toString());
                        if(buffer.hasRemaining()){
                            buffer.compact();
                        }else {
                            buffer.clear();
                        }
                    }
                }else{
                    throw new RuntimeException("connection cannot be established!");
                }
                //关闭连接
                socketChannel.close();
            }else{
                throw new RuntimeException("socket channel cannot be opened!");
            }

        }catch (Exception e){
            throw new  RuntimeException(e);
        }
    }
}
