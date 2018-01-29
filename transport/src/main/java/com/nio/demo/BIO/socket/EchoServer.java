package com.nio.demo.BIO.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer {

    //服务端处理业务逻辑线程池
    private static final ExecutorService executor= Executors.newCachedThreadPool();

    public static void main(String[] args) throws IOException{
        int port = 18081;

        ServerSocket serverSocket  = null;
        try{
            serverSocket = new ServerSocket(port);

            Socket socket = null;
            while (true) {
                socket = serverSocket.accept();
                executor.submit(new EchoServiceHandler(socket));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(serverSocket != null){
                serverSocket.close();
            }
        }

    }
}
