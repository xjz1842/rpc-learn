package com.simple.rpc.framework.register;

import com.simple.rpc.framework.model.InvokerService;
import com.simple.rpc.framework.model.ProviderService;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface IRegisterCenter4Governance {

    /**
     * 获取服务提供者列表与服务消费者列表
     *
     * @param serviceName
     * @param appKey
     * @return
     */
    public Pair<List<ProviderService>, List<InvokerService>> queryProvidersAndInvokers(String serviceName, String appKey);

}
