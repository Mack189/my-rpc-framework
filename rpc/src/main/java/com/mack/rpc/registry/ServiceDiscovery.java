package com.mack.rpc.registry;

import java.net.InetSocketAddress;

/**
 * 服务发现接口
 */
public interface ServiceDiscovery {

    InetSocketAddress lookupService(String serviceName);

}
