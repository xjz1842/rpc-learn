package com.simple.rpc.distribute.lock.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DistributedLock implements Watcher {

    private int threadId;
    private ZooKeeper zk = null;
    private String selfPath;
    private String waitPath;
    private String LOG_PREFIX_OF_THREAD;
    private static final int SESSION_TIMEOUT = 10000;
    private static final String GROUP_PATH = "/disLocks";
    private static final String SUB_PATH = "/disLocks/sub";
    private static final String CONNECTION_STRING = "localhost:2181";

    private static final int THREAD_NUM = 10;

    //确保连接zk成功；
    private CountDownLatch connectedSemaphore = new CountDownLatch(1);

    //确保所有线程运行结束；
    private static final CountDownLatch threadSemaphore = new CountDownLatch(THREAD_NUM);

    public DistributedLock(int id) {
        this.threadId = id;
        LOG_PREFIX_OF_THREAD = "【第" + threadId + "个线程】";
    }

    public static void main(String[] args) {

        for (int i = 0; i < THREAD_NUM; i++) {
            final int threadId = i + 1;

            new Thread() {
                @Override
                public void run() {
                    try {
                        DistributedLock dl = new DistributedLock(threadId);
                        dl.createConnection(CONNECTION_STRING, SESSION_TIMEOUT);

                        //GROUP_PATH不存在的话，由一个线程创建即可；
                        dl.createPath(GROUP_PATH, "该节点由线程"+threadId+"创建", true);
                        System.out.println("执行了create" + GROUP_PATH);
                        dl.getLock();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        try {
            threadSemaphore.await();
            System.out.println("所有线程运行结束!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建节点
     *
     * @param path 节点path
     * @param data 初始数据内容
     * @return
     */
    public boolean createPath(String path, String data, boolean needWatch) throws KeeperException, InterruptedException {
        if (zk.exists(path, needWatch) == null) {
            System.out.println(LOG_PREFIX_OF_THREAD + "节点创建成功, Path: "
                    + this.zk.create(path,
                    data.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT)
                    + ", content: " + data);
        }
        return true;
    }

    private void getLock() throws KeeperException, InterruptedException {
        selfPath = zk.create(SUB_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(LOG_PREFIX_OF_THREAD + "创建锁路径" + selfPath);
        if (checkMinPath()) {
            getLockSuccess();
        }
    }

    /**
     * 检查自己是不是最小的节点
     *
     * @return
     */
    public boolean checkMinPath() throws KeeperException, InterruptedException {
        List<String> subNodes = zk.getChildren(GROUP_PATH, false);

        Collections.sort(subNodes);

        int index = subNodes.indexOf(selfPath.substring(GROUP_PATH.length() + 1));

        switch (index) {
            case -1: {
                System.out.println(LOG_PREFIX_OF_THREAD + "本节点已不在了..." + selfPath);
                return false;
            }
            case 0: {
                System.out.println(LOG_PREFIX_OF_THREAD + "子节点中，我果然是老大" + selfPath);
                return true;
            }
            default: {
                this.waitPath = GROUP_PATH + "/" + subNodes.get(index - 1);
                System.out.println(LOG_PREFIX_OF_THREAD + "获取子节点中，排在我前面的" + waitPath);
                try {
                    zk.getData(waitPath, true, new Stat());
                    return false;
                } catch (KeeperException e) {
                    if (zk.exists(waitPath, false) == null) {
                        System.out.println(LOG_PREFIX_OF_THREAD + "子节点中，排在我前面的" + waitPath + "已失踪，幸福来得太突然?");
                        return checkMinPath();
                    } else {
                        throw e;
                    }
                }
            }
        }
    }

    public void getLockSuccess() throws KeeperException, InterruptedException {

        if (zk.exists(this.selfPath, false) == null) {
            System.out.println(LOG_PREFIX_OF_THREAD + "本节点已不在了...");
            return;
        }

        System.out.println(LOG_PREFIX_OF_THREAD + "获取锁成功，赶紧干活！");
        Thread.sleep(2000);
        System.out.println(LOG_PREFIX_OF_THREAD + "删除本节点：" + selfPath);
        zk.delete(this.selfPath, -1);

        releaseConnection();
        threadSemaphore.countDown();
    }

    /**
     * 创建ZK连接
     *
     * @param connectString  ZK服务器地址列表
     * @param sessionTimeout Session超时时间
     */
    public void createConnection(String connectString, int sessionTimeout) throws IOException, InterruptedException {
        zk = new ZooKeeper(connectString, sessionTimeout, this);
        connectedSemaphore.await();
    }

    /**
     * 关闭ZK连接
     */
    public void releaseConnection() {
        if (this.zk != null) {
            try {
                this.zk.close();
            } catch (InterruptedException e) {
            }
        }
        System.out.println(LOG_PREFIX_OF_THREAD + "释放连接");
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent == null) {
            return;
        }

        Event.KeeperState keeperState = watchedEvent.getState();
        Event.EventType eventType = watchedEvent.getType();

        if (Event.KeeperState.SyncConnected == keeperState) {
            if (Event.EventType.None == eventType) {
                System.out.println(LOG_PREFIX_OF_THREAD + "成功连接上ZK服务器");
                connectedSemaphore.countDown();
            } else if (watchedEvent.getType() == Event.EventType.NodeDeleted && watchedEvent.getPath().equals(waitPath)) {
                System.out.println(LOG_PREFIX_OF_THREAD + "收到情报，排我前面的家伙已挂，我是不是可以出山了？");
                try {
                    if (checkMinPath()) {
                        getLockSuccess();
                    }
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (Event.KeeperState.Disconnected == keeperState) {
            System.out.println(LOG_PREFIX_OF_THREAD + "与ZK服务器断开连接");
        } else if (Event.KeeperState.AuthFailed == keeperState) {
            System.out.println(LOG_PREFIX_OF_THREAD + "权限检查失败");
        } else if (Event.KeeperState.Expired == keeperState) {
            System.out.println(LOG_PREFIX_OF_THREAD + "会话失效");
        }
    }
}
