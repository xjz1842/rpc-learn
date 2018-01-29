package com.simple.rpc.framework.revoker;

import com.google.common.collect.Maps;
import com.simple.rpc.framework.model.RpcResponse;
import com.simple.rpc.framework.model.RpcResponseWrapper;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RevokerResponseHolder {

    //服务返回结果Map
    private static final Map<String, RpcResponseWrapper> responseMap = Maps.newConcurrentMap();

    //清除过期的返回结果
    private static final ExecutorService removeExpireKeyExecutor = Executors.newSingleThreadExecutor();

    static {
        //删除超时未获取到结果的key,防止内存泄露
        removeExpireKeyExecutor.execute(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try{
                        for (Map.Entry<String, RpcResponseWrapper> entry : responseMap.entrySet()) {

                            boolean isExpire = entry.getValue().isExpire();

                            if(isExpire){
                                responseMap.remove(entry.getKey());
                            }
                            Thread.sleep(10);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    /**
     * 初始化返回结果容器,requestUniqueKey唯一标识本次调用
     *
     * @param requestUniqueKey
     */
    public static void initResponseData(String requestUniqueKey) {
        responseMap.put(requestUniqueKey, RpcResponseWrapper.of());
    }

    /**
     * 将Netty调用异步返回结果放入阻塞队列
     *
     * @param response
     */
    public static void putResultValue(RpcResponse response) {
        long currentTime = System.currentTimeMillis();
        RpcResponseWrapper responseWrapper = responseMap.get(response.getReuqestId());
        responseWrapper.setResponseTime(currentTime);
        responseWrapper.getResponseQueue().add(response);
        responseMap.put(response.getReuqestId(), responseWrapper);
    }

    /**
     * 从阻塞队列中获取Netty异步返回的结果值
     *
     * @param requestUniqueKey
     * @param timeout
     * @return
     */
    public static RpcResponse getValue(String requestUniqueKey, long timeout) {
        RpcResponseWrapper responseWrapper = responseMap.get(requestUniqueKey);
        try {
            return responseWrapper.getResponseQueue().poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            responseMap.remove(requestUniqueKey);
        }
    }
}
