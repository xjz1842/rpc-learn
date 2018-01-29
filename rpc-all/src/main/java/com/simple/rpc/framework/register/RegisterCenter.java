package com.simple.rpc.framework.register;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.simple.rpc.framework.helper.IPHelper;
import com.simple.rpc.framework.helper.PropertyConfigeHelper;
import com.simple.rpc.framework.model.InvokerService;
import com.simple.rpc.framework.model.ProviderService;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterCenter implements IRegisterCenter4Invoker, IRegisterCenter4Provider, IRegisterCenter4Governance {

    private static RegisterCenter registerCenter = new RegisterCenter();

    //服务提供者列表,Key:服务提供者接口  value:服务提供者服务方法列表
    private static final Map<String, List<ProviderService>> providerServiceMap = new ConcurrentHashMap<>();

    //服务端ZK服务元信息,选择服务(第一次直接从ZK拉取,后续由ZK的监听机制主动更新)
    private static final Map<String, List<ProviderService>> serviceMetaDataMap4Consume = new ConcurrentHashMap<>();

    private static String ZK_SERVICE = PropertyConfigeHelper.getZkService();

    private static int ZK_SESSION_TIME_OUT = PropertyConfigeHelper.getZkConnectionTimeout();
    private static int ZK_CONNECTION_TIME_OUT = PropertyConfigeHelper.getZkConnectionTimeout();

    private static String ROOT_PATH = "/config_register";

    public static String PROVIDER_TYPE = "provider";
    public static String INVOKER_TYPE = "consumer";

    private static volatile ZkClient zkClient = null;

    private RegisterCenter() {

    }

    public static RegisterCenter getInstance() {
        return registerCenter;
    }

    @Override
    public Pair<List<ProviderService>, List<InvokerService>> queryProvidersAndInvokers(String serviceName, String appKey) {
        //服务消费者列表
        List<InvokerService> invokerServices = Lists.newArrayList();

        //服务提供者服务列表
        List<ProviderService> providerServices = Lists.newArrayList();

        //连接zk
        if(zkClient == null){
            synchronized (RegisterCenter.class){
                if(zkClient == null){
                    zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT, new SerializableSerializer());
                }
            }
        }
        String parentPath = ROOT_PATH + "/" + appKey;

        //获取ROOT_PATH + APP_KEY注册中心的目录列表
        List<String> groupServiceList = zkClient.getChildren(parentPath);

        if(CollectionUtils.isEmpty(groupServiceList)){
            return Pair.of(providerServices,invokerServices);
        }

        for(String group : groupServiceList){
            String groupPath = parentPath + "/" + group;
            //获取ROOT_PATH + APP_KEY + group 注册中心子目录列表
            List<String> serviceList = zkClient.getChildren(groupPath);

            if(CollectionUtils.isEmpty(serviceList)){
                continue;
            }

            for(String service : serviceList){
                //获取ROOT_PATH + APP_LEY + group + service 注册中心目录列表
                String servicePath = groupPath + "/" + service;
                List<String> serivceTypes = zkClient.getChildren(servicePath);
                if(CollectionUtils.isEmpty(serivceTypes)){
                    continue;
                }

                for(String serviceType : serivceTypes){
                    if(StringUtils.equals(serviceType,PROVIDER_TYPE)){
                    // root_PATH + APP_KEY + group + service + serviceType 注册中心目录列表
                     String providerPath = servicePath + "/" + serviceType;

                     List<String> providers = zkClient.getChildren(providerPath);

                     if(CollectionUtils.isEmpty(providers)){
                         continue;
                     }

                     //获取服务器提供者信息
                      for(String provider : providers){
                         String[] providerNodeArr  = StringUtils.split(provider,"|");

                         ProviderService providerService = new ProviderService();
                         providerService.setAppKey(appKey);
                         providerService.setGroupName(group);
                         providerService.setServerIp(providerNodeArr[0]);
                         providerService.setServerPort(Integer.parseInt(providerNodeArr[1]));
                         providerService.setWeight(Integer.parseInt(providerNodeArr[2]));
                         providerService.setWorkerThreads(Integer.parseInt(providerNodeArr[3]));
                         providerServices.add(providerService);
                      }
                    }else if(StringUtils.equals(serviceType,INVOKER_TYPE)){
                        //获取 ROO _PATH + APP_KEY + group + service + serviceTupe注册中心
                        String invokerPath = servicePath + "/" + serviceType;

                        List<String> invokers = zkClient.getChildren(invokerPath);

                        if(CollectionUtils.isEmpty(invokers)){
                            continue;
                        }
                        //获取服务消费者信息
                        for(String invoker : invokers){
                            InvokerService invokerService = new InvokerService();
                            invokerService.setRemoteAppKey(appKey);
                            invokerService.setGroupName(group);
                            invokerService.setInvokerIp(invoker);
                            invokerServices.add(invokerService);
                        }
                    }
                }
            }
        }
        return Pair.of(providerServices,invokerServices);
    }

    @Override
    public void initProviderMap(String remoteAppKey, String groupName) {
        if (MapUtils.isEmpty(serviceMetaDataMap4Consume)) {
            serviceMetaDataMap4Consume.putAll(fetchOrUpdateServiceMetaData(remoteAppKey, groupName));
        }
    }

    @Override
    public Map<String, List<ProviderService>> getServiceMetaDataMap4Consume() {
        return serviceMetaDataMap4Consume;
    }

    @Override
    public Map<String, List<ProviderService>> getProviderServiceMap() {
        return providerServiceMap;
    }

    @Override
    public void registerInvoker(InvokerService invoker) {
        if (invoker == null) {
            return;
        }

        //连接zk,注册服务
        synchronized (RegisterCenter.class) {

            if(zkClient == null){
                zkClient = new ZkClient(ZK_SERVICE,ZK_SESSION_TIME_OUT,ZK_CONNECTION_TIME_OUT);
            }

            //创建 ZK命名的空间/ 前部署应用APP命名空间 /
            boolean exist = zkClient.exists(ROOT_PATH);
            if (!exist) {
                zkClient.createPersistent(ROOT_PATH, true);
            }

            //创建服务消费者节点
            String remoteAppKey = invoker.getRemoteAppKey();
            String groupName = invoker.getGroupName();
            String serviceNode = invoker.getServiceInterface().getName();
            String servicePath = ROOT_PATH + "/" + remoteAppKey + "/" + groupName + "/" + serviceNode+"/"+INVOKER_TYPE;

            exist = zkClient.exists(servicePath);
            if(!exist){
                zkClient.createPersistent(servicePath,true);
            }

            //创建当前服务的节点
            String localIp = IPHelper.localIp();

            String currentServiceNode = servicePath+"/"+localIp;

            if(!exist){
                //创建临时节点
                zkClient.createEphemeral(currentServiceNode);
            }
        }
    }

    @Override
    public void registerProvider(List<ProviderService> serviceMetaData) {
        if (CollectionUtils.isEmpty(serviceMetaData)) {
            return;
        }

        //链接zk, 注册服务
        synchronized (RegisterCenter.class) {

            for (ProviderService providerService : serviceMetaData) {
                String serviceInterfaceName = providerService.getServiceInteface().getName();
                List<ProviderService> providers = providerServiceMap.get(serviceInterfaceName);

                if (CollectionUtils.isEmpty(providers)) {
                    providers = new ArrayList<>();
                }
                providers.add(providerService);
                providerServiceMap.put(serviceInterfaceName, providers);
            }

            if (zkClient == null) {
                zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT);
            }

            //创建zookeeper的节点
            String APP_KEY = serviceMetaData.get(0).getAppKey();

            String ZK_PATH = ROOT_PATH + "/" + APP_KEY;

            boolean exist = zkClient.exists(ZK_PATH);
            if (!exist) {
                zkClient.createPersistent(ZK_PATH, true);
            }

            //创建zookeeper的节点
            for (Map.Entry<String, List<ProviderService>> entry : providerServiceMap.entrySet()) {
                String groupName = entry.getValue().get(0).getGroupName();
                //创建服务提供者
                String serviceNode = entry.getKey();

                String servicePath = ZK_PATH + "/" + groupName + "/" + serviceNode + "/" + PROVIDER_TYPE;
                exist = zkClient.exists(servicePath);
                if (!exist) {
                    zkClient.createPersistent(servicePath, true);
                }

                //创建当前服务节点
                int serverPort = entry.getValue().get(0).getServerPort();//服务端口
                int weight = entry.getValue().get(0).getWeight();//服务权重
                int workerThreads = entry.getValue().get(0).getWorkerThreads();//服务工作线程

                String localIp = IPHelper.localIp();
                String currentServiceIpNode = servicePath + "/" + localIp + "|" + serverPort + "|" + weight + "|" + workerThreads + "|" + groupName;
                exist = zkClient.exists(currentServiceIpNode);

                if (!exist) {
                    //注意,这里创建的是临时节点
                    zkClient.createEphemeral(currentServiceIpNode);
                }

                zkClient.subscribeChildChanges(servicePath, new IZkChildListener() {
                    @Override
                    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {

                        if (currentChilds == null) {
                            currentChilds = Lists.newArrayList();
                        }

                        //存活的服务IP列表
                        List<String> activityServiceIpList = Lists.newArrayList(Lists.transform(currentChilds, new Function<String, String>() {
                            @Override
                            public String apply(String input) {
                                return StringUtils.split(input, "|")[0];
                            }
                        }));
                        refreshActivityService(activityServiceIpList);
                    }
                });
            }
        }
    }

    //利用ZK自动刷新当前存活的服务提供者列表数据
    private void refreshActivityService(List<String> serviceIpList) {

        if (serviceIpList == null) {
            serviceIpList = Lists.newArrayList();
        }

        Map<String, List<ProviderService>> currentServiceMetaDataMap = Maps.newHashMap();

        for (Map.Entry<String, List<ProviderService>> entry : providerServiceMap.entrySet()) {
            String key = entry.getKey();
            List<ProviderService> providerServices = entry.getValue();

            List<ProviderService> serviceMetaDataModelList = currentServiceMetaDataMap.get(key);
            if (serviceMetaDataModelList == null) {
                serviceMetaDataModelList = Lists.newArrayList();
            }

            for (ProviderService providerService : providerServices) {
                if (serviceIpList.contains(providerService.getServerIp())) {
                    serviceMetaDataModelList.add(providerService);
                }
            }
            currentServiceMetaDataMap.put(key, serviceMetaDataModelList);
        }

        providerServiceMap.clear();
        System.out.println("currentServiceMetaDataMap," + JSON.toJSONString(currentServiceMetaDataMap));
        providerServiceMap.putAll(currentServiceMetaDataMap);
    }

    private Map<String, List<ProviderService>> fetchOrUpdateServiceMetaData(String remoteAppKey, String groupName) {
        final Map<String, List<ProviderService>> providerServiceMap = Maps.newConcurrentMap();

        //连接zk
        synchronized (RegisterCenter.class) {
            if (zkClient == null) {
                zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT, new SerializableSerializer());
            }
        }

        //从ZK拉取服务提供者列表
        String providerPath = ROOT_PATH + "/" + remoteAppKey + "/" + groupName;

        List<String> providerServices = zkClient.getChildren(providerPath);

        for (String serviceName : providerServices) {
            String servicePath = providerPath + "/" + serviceName + "/" + PROVIDER_TYPE;

            List<String> ipPathList = zkClient.getChildren(servicePath);

            for (String ipPath : ipPathList) {
                String serverIp = StringUtils.split(ipPath, "|")[0];
                String serverPort = StringUtils.split(ipPath, "|")[1];
                int weight = Integer.parseInt(StringUtils.split(ipPath, "|")[2]);
                int workerThreads = Integer.parseInt(StringUtils.split(ipPath, "|")[3]);
                String group = StringUtils.split(ipPath, "|")[4];

                List<ProviderService> providerServiceList = providerServiceMap.get(serviceName);

                if (providerServiceList == null) {
                    providerServiceList = Lists.newArrayList();
                }
                ProviderService providerService = new ProviderService();
                try {
                    providerService.setServiceInteface(ClassUtils.getClass(serviceName));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                providerService.setServerIp(serverIp);
                providerService.setServerPort(Integer.parseInt(serverPort));
                providerService.setWeight(weight);
                providerService.setWorkerThreads(workerThreads);
                providerService.setGroupName(group);
                providerServiceList.add(providerService);

                providerServiceMap.put(serviceName, providerServiceList);
            }

            //监听注册服务的变化,同时更新数据到本地缓存
            zkClient.subscribeChildChanges(servicePath, new IZkChildListener() {
                @Override
                public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                    if (currentChilds == null) {
                        currentChilds = Lists.newArrayList();
                    }
                    currentChilds = Lists.newArrayList(Lists.transform(currentChilds, new Function<String, String>() {
                        @Override
                        public String apply(String input) {
                            return StringUtils.split(input, "|")[0];
                        }
                    }));
                    refreshServiceMetaDataMap(currentChilds);
                }
            });
        }
        return providerServiceMap;
    }

    private void refreshServiceMetaDataMap(List<String> serviceIpList) {
        if (serviceIpList == null) {
            serviceIpList = Lists.newArrayList();
        }

        Map<String,List<ProviderService>> currentServiceMeteDataMap = Maps.newHashMap();

        for(Map.Entry<String,List<ProviderService>> entry : serviceMetaDataMap4Consume.entrySet()){
            String serviceInterfaceKey = entry.getKey();
            List<ProviderService> serviceList = entry.getValue();

            List<ProviderService> providerServiceList = currentServiceMeteDataMap.get(serviceInterfaceKey);

            if(providerServiceList == null){
                providerServiceList = Lists.newArrayList();
            }
            for(ProviderService serviceMetaData : serviceList){
                if(serviceIpList.contains(serviceMetaData.getServerIp())){
                   providerServiceList.add(serviceMetaData);
                }
            }
            currentServiceMeteDataMap.put(serviceInterfaceKey,providerServiceList);
        }
    }

}
