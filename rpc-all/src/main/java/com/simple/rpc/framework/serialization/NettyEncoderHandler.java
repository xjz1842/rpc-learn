package com.simple.rpc.framework.serialization;

import com.simple.rpc.framework.serialization.common.SerializeType;
import com.simple.rpc.framework.serialization.engine.SerializerEngine;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author xjz
 *
 */
public class NettyEncoderHandler extends MessageToByteEncoder {

    //序列化类型
    private SerializeType serializeType;

    public NettyEncoderHandler(SerializeType serializeType) {
        this.serializeType = serializeType;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {

        //将对象序列化为字节数组
       byte[] data = SerializerEngine.serialize(o,serializeType.getSerializeType());

        //将字节数组(消息体)的长度作为消息头写入,解决半包/粘包问题
        byteBuf.writeInt(data.length);
        //写入序列化后得到的字节数组
        byteBuf.writeBytes(data);

    }
}
