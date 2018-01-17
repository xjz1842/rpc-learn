package com.simple.rpc.socket.client.impl;

import com.simple.rpc.socket.client.UserService;

public class UserServiceImpl  implements UserService{

    @Override
    public String getUserInfo() {

        return "xiao ming";
    }
}
