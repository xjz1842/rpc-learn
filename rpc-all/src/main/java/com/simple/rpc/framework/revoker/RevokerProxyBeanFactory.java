package com.simple.rpc.framework.revoker;

import com.simple.rpc.framework.model.ProviderService;
import com.simple.rpc.framework.model.RpcRequest;
import com.simple.rpc.framework.model.RpcResponse;
import com.simple.rpc.framework.register.IRegisterCenter4Invoker;
import com.simple.rpc.framework.register.RegisterCenter;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class RevokerProxyBeanFactory implements InvocationHandler {

    private ExecutorService fixedThreadPool = null;

    //服务接口
    private Class<?> targetInterface;
    //超时时间
    private int consumeTimeout;
    //调用者线程数
    private static int threadWorkerNumber = 10;
    //负载均衡策略
    private String clusterStrategy;

    private static volatile RevokerProxyBeanFactory singleton;

    public RevokerProxyBeanFactory(Class<?> targetInterface, int consumeTimeout, String clusterStrategy) {
        this.targetInterface = targetInterface;
        this.consumeTimeout = consumeTimeout;
        this.clusterStrategy = clusterStrategy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //服务接口名称
        String serviceKey = targetInterface.getName();

        //获取某个接口的服务提供者列表
        IRegisterCenter4Invoker registerCenter4Consumer = RegisterCenter.getInstance();
        List<ProviderService> providerServices = registerCenter4Consumer.getServiceMetaDataMap4Consume().get(serviceKey);

        //根据软负载策略,从服务提供者列表选取本次调用的服务提供者
        if (CollectionUtils.isEmpty(providerServices)) {
            throw new RuntimeException(serviceKey + "{}没有找到服务提供者");
        }

        ProviderService providerService = providerServices.get(0);
        //复制一份服务提供者信息
        ProviderService newProvider = providerService.copy();

        //设置本次调用服务的方法以及接口
        newProvider.setServiceMethod(method);
        newProvider.setServiceInteface(targetInterface);

        //声明调用AresRequest对象,AresRequest表示发起一次调用所包含的信息
        final RpcRequest rpcRequest = new RpcRequest();

        //设置本次调用的唯一标识
        rpcRequest.setReuqestId(UUID.randomUUID().toString()+"-"+Thread.currentThread().getId());
        //设置本次调用的服务提供者信息
        rpcRequest.setProviderService(newProvider);
        //设置本次调用的超时时间
        rpcRequest.setInvokeTimeout(consumeTimeout);
        //设置本次调用的方法名称
        rpcRequest.setMethodName(method.getName());
        //设置本次调用的方法参数信息
        rpcRequest.setArgs(args);

        try{
            //构建用来发起调用的线程池
            if(fixedThreadPool == null) {
                synchronized (RevokerProxyBeanFactory.class) {
                    if (fixedThreadPool == null) {
                        fixedThreadPool = Executors.newFixedThreadPool(threadWorkerNumber);
                    }
                }
            }
            //根据服务提供者的ip,port,构建InetSocketAddress对象,标识服务提供者地址
            String serviceIp = rpcRequest.getProviderService().getServerIp();
            int serverPort = rpcRequest.getProviderService().getServerPort();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(serviceIp,serverPort);

            //提交本次调用信息到线程次FixedThreadPool 发起调用
            Future<RpcResponse> responseFuture = fixedThreadPool.submit(RevokerServiceCallable.of(inetSocketAddress,rpcRequest));

            //获取调用的返回结果
            RpcResponse response = responseFuture.get(rpcRequest.getInvokeTimeout(), TimeUnit.MILLISECONDS);
            if (response != null) {
                return response.getResult();
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return null;
    }

    public Object getProxy() {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{targetInterface}, this);
    }

    public static RevokerProxyBeanFactory singleton(Class<?> targetInterface, int consumeTimeout, String clusterStrategy) throws Exception {
        if (null == singleton) {
            synchronized (RevokerProxyBeanFactory.class) {
                if (null == singleton) {
                    singleton = new RevokerProxyBeanFactory(targetInterface, consumeTimeout, clusterStrategy);
                }
            }
        }
        return singleton;
    }

}
