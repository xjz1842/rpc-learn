package com.simple.rpc.register;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ServiceProvoder {

    private static String rootPath = "/service-root";
    private static ZkClient zkClient;
    private static String zkString = "localhost:2181";

    private static String serviceNode = "service";
    private static List<String> nodeList = new ArrayList<>();

    private CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        int timeOut = 30000;
        ZkClient zkClient = new ZkClient(zkString, timeOut);

        if (!zkClient.exists(rootPath)) {
            zkClient.createPersistent(rootPath);
        }

        for (int i = 0; i < 3; i++) {
            if(!zkClient.exists(rootPath + "/" + serviceNode + "-" + i)) {
                zkClient.createPersistent(rootPath + "/" + serviceNode + "-" + i, "192.168.0." + i);
            }
            nodeList.add(rootPath + "/" + serviceNode + "-" + i);
        }

        zkClient.subscribeChildChanges(rootPath, new IZkChildListener() {
            @Override
            public void handleChildChange(String s, List<String> list) throws Exception {
                System.out.println("s" + s);
                nodeList = list;

                System.out.println(nodeList);
            }
        });

        zkClient.delete(rootPath + "/" + serviceNode + "-" +1);

        zkClient.close();
    }
}
