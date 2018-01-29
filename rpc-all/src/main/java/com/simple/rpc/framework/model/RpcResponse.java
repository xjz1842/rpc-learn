package com.simple.rpc.framework.model;


import java.io.Serializable;

public class RpcResponse implements Serializable {

    //唯一标识一次返回值
    private String reuqestId;

    //客户端指定的服务超时时间
    private long invokeTimeout;

    //接口调用返回的结果对象
    private Object result;

    public String getReuqestId() {
        return reuqestId;
    }

    public void setReuqestId(String reuqestId) {
        this.reuqestId = reuqestId;
    }

    public long getInvokeTimeout() {
        return invokeTimeout;
    }

    public void setInvokeTimeout(long invokeTimeout) {
        this.invokeTimeout = invokeTimeout;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

}
