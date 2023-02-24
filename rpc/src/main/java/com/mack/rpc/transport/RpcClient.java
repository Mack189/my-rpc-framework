package com.mack.rpc.transport;

import com.mack.rpc.serializer.CommonSerializer;
import mack.entity.RpcRequest;

public interface RpcClient {

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    Object sendRequest(RpcRequest rpcRequest);
}
