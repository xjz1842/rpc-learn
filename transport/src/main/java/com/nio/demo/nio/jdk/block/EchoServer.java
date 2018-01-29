package com.nio.demo.nio.jdk.block;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/***
 * jdk自带的NIO库
 */
public class EchoServer {

    //执行服务端业务逻辑线程池
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static void main(String[] args) throws Exception{
        //新建服务端serverSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //如果serverSocketChannel创建成功
        if(serverSocketChannel.isOpen()){
            //设置为阻塞模式
            serverSocketChannel.configureBlocking(true);

            //设置网络传输参数
            serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
            serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            //绑定服务端Channel端口与本地IP
            serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 8082));

            while (true) {
                try {
                    //等待连接客户端的请求
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    //提交给线程池处理业务逻辑
                    executor.submit(new EchoHandler(socketChannel));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
