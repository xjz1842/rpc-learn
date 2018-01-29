package com.simple.rpc.framework.model;

import java.io.Serializable;

public class RpcRequest implements Serializable {

    //唯一标识一次返回值
    private String reuqestId;

    //服务提供者信息
    private ProviderService providerService;

    //调用的方法名称
    private String methodName;

    //传递参数
    private Object[] args;

    //消费端应用名
    private String applicationName;

    //消费请求超时时长
    private long invokeTimeout;

    public String getReuqestId() {
        return reuqestId;
    }

    public void setReuqestId(String reuqestId) {
        this.reuqestId = reuqestId;
    }

    public ProviderService getProviderService() {
        return providerService;
    }

    public void setProviderService(ProviderService providerService) {
        this.providerService = providerService;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public long getInvokeTimeout() {
        return invokeTimeout;
    }

    public void setInvokeTimeout(long invokeTimeout) {
        this.invokeTimeout = invokeTimeout;
    }
}
