package com.rpc.demo.rmiInvoker.rpc;

import com.rpc.demo.common.User;
import com.rpc.demo.common.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class RmiClient {

    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("rmi-rpc-client.xml");
        UserService userService = context.getBean("userRmiServiceProxy",UserService.class);
        User user = userService.findByName("lisi");
        System.out.println(user.getName() + "   " + user.getEmail());
    }

//    @Bean
//    RmiProxyFactoryBean rmiProxyFactoryBean() {
//        RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
//        rmiProxyFactoryBean.setServiceUrl("rmi://127.0.0.1:1099/userRmiService");
//        rmiProxyFactoryBean.setServiceInterface(UserService.class);
//        return rmiProxyFactoryBean;
//    }

}
