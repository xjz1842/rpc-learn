package com.rpc.demo.hessionInvoker.rpc;

import com.rpc.demo.common.User;
import com.rpc.demo.common.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HessianClient {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("hessian-rpc-client.xml");
        UserService userService = context.getBean("userServiceHessianProxy",UserService.class);
        User user = userService.findByName("lisi");
        System.out.println(user.getName() + "   " + user.getEmail());
    }
}
