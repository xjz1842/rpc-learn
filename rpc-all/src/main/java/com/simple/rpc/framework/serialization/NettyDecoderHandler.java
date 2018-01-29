package com.simple.rpc.framework.serialization;

import com.simple.rpc.framework.serialization.common.SerializeType;
import com.simple.rpc.framework.serialization.engine.SerializerEngine;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author xjz
 * 解码器
 */
public class NettyDecoderHandler extends ByteToMessageDecoder {

    //解码对象class
    private Class<?> clazz;
    //解码对象编码所使用序列化类型
    private SerializeType serializeType;

    public NettyDecoderHandler(Class<?> clazz, SerializeType serializeType) {
        this.clazz = clazz;
        this.serializeType = serializeType;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        //获取消息头所标识的消息体字节数组长度
        if (byteBuf.readableBytes() < 4) {
            return;
        }

        byteBuf.markReaderIndex();
        int dataLength = byteBuf.readInt();

        //若当前可以获取到的字节数小于实际长度,则直接返回,直到当前可以获取到的字节数等于实际长度
        if(byteBuf.readableBytes() < dataLength){
            byteBuf.resetReaderIndex();
            return;
        }

        //读取完整的消息体字节数组
        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);

        //将字节数组反序列化为java对象(SerializerEngine参考序列化与反序列化章节)
        Object obj =  SerializerEngine.deserialize(data,clazz,serializeType.getSerializeType());

        list.add(obj);
    }
}
