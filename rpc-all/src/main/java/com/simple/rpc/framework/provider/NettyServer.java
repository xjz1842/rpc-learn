package com.simple.rpc.framework.provider;

import com.simple.rpc.framework.helper.PropertyConfigeHelper;
import com.simple.rpc.framework.model.RpcRequest;
import com.simple.rpc.framework.serialization.NettyDecoderHandler;
import com.simple.rpc.framework.serialization.NettyEncoderHandler;
import com.simple.rpc.framework.serialization.common.SerializeType;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyServer {

    private static NettyServer nettyServer = new NettyServer();

    private Channel channel;
    ////服务端boss线程组
    private EventLoopGroup bossGroup;
    //服务端worker线程组
    private EventLoopGroup workerGroup;
    //序列化类型配置信息
    private SerializeType serializeType = PropertyConfigeHelper.getSerializeType();

    private NettyServer() {
    }

    public static NettyServer getInstance() {
        return nettyServer;
    }

    /**
     * 启动Netty服务
     *
     * @param port
     */
    public void start(int port) {

        synchronized (NettyServer.class) {
            if (bossGroup != null || workerGroup != null) {
                return;
            }
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();

            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                            //注册解码器NettyDecoderHandler
                            socketChannel.pipeline().addLast(new NettyDecoderHandler(RpcRequest.class, serializeType));
                            //注册解码器NettyEncoderHandler
                            socketChannel.pipeline().addLast(new NettyEncoderHandler(serializeType));

                            //注册服务端业务逻辑处理器NettyServerInvokeHandler
                            socketChannel.pipeline().addLast(new NettyServerInvokeHandler());
                        }
                    });

            try {
                channel = serverBootstrap.bind(port).sync().channel();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 停止Netty服务
     */
    public void stop() {
        if (null == channel) {
            throw new RuntimeException("Netty Server Stoped");
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        channel.closeFuture().syncUninterruptibly();
    }



}
