package com.rpc.demo.common;


import java.util.HashMap;
import java.util.Map;


public class UserServiceImpl implements UserService {

    private static final Map<String,User> userMap = new HashMap<>();

    static {
        userMap.put("zhnagshan",new User("zhnagshan","zhnagshan@163.com"));
        userMap.put("lisi",new User("lisi","lisi@163.com"));
    }

    @Override
    public User findByName(String name) {
        return userMap.get(name);
    }
}
