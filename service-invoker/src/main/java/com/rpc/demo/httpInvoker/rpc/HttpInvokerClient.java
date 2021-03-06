package com.rpc.demo.httpInvoker.rpc;

import com.rpc.demo.common.User;
import com.rpc.demo.common.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HttpInvokerClient {

    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("httpinvoker-rpc-client.xml");
        UserService userService = context.getBean("userServiceProxy",UserService.class);
        User user = userService.findByName("lisi");
        System.out.println(user.getName() + "   " + user.getEmail());
    }
}
