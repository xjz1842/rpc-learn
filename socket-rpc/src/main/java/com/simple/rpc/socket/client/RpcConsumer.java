package com.simple.rpc.socket.client;

import com.simple.rpc.socket.server.RpcFramework;

public class RpcConsumer {

    public static void main(String[] args)throws Exception{

        UserService userService = RpcFramework.refer(UserService.class,"127.0.0.1",1234);

        for (int i = 0; i < 1000; i ++) {
            String hello = userService.getUserInfo();
            System.out.println(hello);
            Thread.sleep(1000);
        }
    }




}
