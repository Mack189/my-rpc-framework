package com.mack.rpc.transport.netty.codec;

import com.mack.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import mack.entity.RpcRequest;
import mack.enumeration.PackageType;

/**
 * 通用的编码拦截器
 */
public class CommonEncoder extends MessageToByteEncoder {

//        +---------------+---------------+-----------------+-------------+
//        |  Magic Number |  Package Type | Serializer Type | Data Length |
//        |    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
//        +---------------+---------------+-----------------+-------------+
//        |                          Data Bytes                           |
//        |                   Length: ${Data Length}                      |
//        +---------------------------------------------------------------+


    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        out.writeInt(MAGIC_NUMBER);
        if (msg instanceof RpcRequest) {
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        } else {
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        out.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
