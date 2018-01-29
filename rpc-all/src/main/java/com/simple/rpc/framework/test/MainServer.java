package com.simple.rpc.framework.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainServer {

    private static final Logger logger = LoggerFactory.getLogger(MainServer.class);

    public static void main(String[] args) {
        //发布服务
        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("server.xml");
        System.out.println(" 服务发布完成");
    }
}
