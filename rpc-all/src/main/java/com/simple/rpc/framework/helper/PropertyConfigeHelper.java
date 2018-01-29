package com.simple.rpc.framework.helper;

import com.simple.rpc.framework.serialization.common.SerializeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyConfigeHelper {

    private static final Logger logger = LoggerFactory.getLogger(PropertyConfigeHelper.class);
    private static final String PROPERTY_CLASSPATH = "/Rpc.properties";
    private static final Properties properties = new Properties();

    //ZK服务地址
    private static String zkService = "";
    //ZK session超时时间
    private static int zkSessionTimeout;
    //ZK connection超时时间
    private static int zkConnectionTimeout;
    //序列化算法类型
    private static SerializeType serializeType;
    //每个服务端提供者的Netty的连接数
    private static int channelConnectSize;

    /**
     * 初始化
     */
    static {
        InputStream inputStream = null;
        try {
            inputStream = PropertyConfigeHelper.class.getResourceAsStream(PROPERTY_CLASSPATH);
            if (null == inputStream) {
                throw new IllegalStateException("Rpc.properties can not found in the classpath.");
            }
            properties.load(inputStream);

            zkService = properties.getProperty("zk_service");
            zkSessionTimeout = Integer.parseInt(properties.getProperty("zk_sessionTimeout", "500"));
            zkConnectionTimeout = Integer.parseInt(properties.getProperty("zk_connectionTimeout", "500"));
            channelConnectSize = Integer.parseInt(properties.getProperty("channel_connect_size", "10"));
            String seriType = properties.getProperty("serialize_type");
            serializeType = SerializeType.queryByType(seriType);
            if (serializeType == null) {
                throw new RuntimeException("serializeType is null");
            }
        }catch (IOException e){
            logger.warn("load rpc's properties file failed.", e);
            throw new RuntimeException(e);
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    public static String getPropertyClasspath() {
        return PROPERTY_CLASSPATH;
    }

    public static Properties getProperties() {
        return properties;
    }

    public static String getZkService() {
        return zkService;
    }

    public static void setZkService(String zkService) {
        PropertyConfigeHelper.zkService = zkService;
    }

    public static int getZkSessionTimeout() {
        return zkSessionTimeout;
    }

    public static void setZkSessionTimeout(int zkSessionTimeout) {
        PropertyConfigeHelper.zkSessionTimeout = zkSessionTimeout;
    }

    public static int getZkConnectionTimeout() {
        return zkConnectionTimeout;
    }

    public static void setZkConnectionTimeout(int zkConnectionTimeout) {
        PropertyConfigeHelper.zkConnectionTimeout = zkConnectionTimeout;
    }

    public static SerializeType getSerializeType() {
        return serializeType;
    }

    public static void setSerializeType(SerializeType serializeType) {
        PropertyConfigeHelper.serializeType = serializeType;
    }

    public static int getChannelConnectSize() {
        return channelConnectSize;
    }

    public static void setChannelConnectSize(int channelConnectSize) {
        PropertyConfigeHelper.channelConnectSize = channelConnectSize;
    }
}
