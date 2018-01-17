package com.simple.rpc.socket.server;

import com.simple.rpc.socket.client.UserService;
import com.simple.rpc.socket.client.impl.UserServiceImpl;

public class Servcer {

    public static void main(String[] args)throws Exception {

        UserService service = new UserServiceImpl();
        RpcFramework.export(service, 1234);

    }

}
