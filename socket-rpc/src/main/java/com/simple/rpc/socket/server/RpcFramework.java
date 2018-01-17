package com.simple.rpc.socket.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

public class RpcFramework {

    /**
     * 暴露服务
     *
     * @param service 服务实现
     * @param port    服务端口
     * @throws Exception
     */
    public static void export(Object service, int port) throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);

        for (; ; ) {
            try {
                Socket socket = serverSocket.accept();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            try {
                                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                                try {
                                    String methodName = objectInputStream.readUTF();
                                    Class<?>[] parameterTypes = (Class<?>[]) objectInputStream.readObject();
                                    Object[] arguments = (Object[]) objectInputStream.readObject();

                                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                                    try {
                                        Method method = service.getClass().getMethod(methodName, parameterTypes);
                                        Object result = method.invoke(service, arguments);
                                        output.writeObject(result);
                                    } catch (Throwable t) {
                                        output.writeObject(t);
                                    } finally {
                                        output.close();
                                    }
                                } finally {
                                    objectInputStream.close();
                                }
                            } finally {
                                socket.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 引用服务
     *
     * @param <T>            接口泛型
     * @param interfaceClass 接口类型
     * @param host           服务器主机名
     * @param port           服务器端口
     * @return 远程服务
     * @throws Exception
     */
    public static <T> T refer(final Class<T> interfaceClass, final String host, final int port) throws Exception {

        if (interfaceClass == null) {
            throw new IllegalArgumentException("Interface class == nul");
        }
        if (!interfaceClass.isInterface())
            throw new IllegalArgumentException("The " + interfaceClass.getName() + " must be interface class!");
        if (host == null || host.length() == 0)
            throw new IllegalArgumentException("Host == null!");
        if (port <= 0 || port > 65535)
            throw new IllegalArgumentException("Invalid port " + port);

        System.out.println("Get remote service " + interfaceClass.getName() + " from server " + host + ":" + port);

        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                Socket socket = new Socket(host, port);
                try {
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    try {
                        out.writeUTF(method.getName());
                        out.writeObject(method.getParameterTypes());
                        out.writeObject(args);

                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

                        try {
                            Object result = input.readObject();
                            if (result instanceof Throwable) {
                                throw (Throwable) result;
                            }
                            return result;
                        } finally {
                            input.close();
                        }
                    } finally {
                        out.close();
                    }
                }finally {
                    socket.close();
                }
            }
        });
    }


}
