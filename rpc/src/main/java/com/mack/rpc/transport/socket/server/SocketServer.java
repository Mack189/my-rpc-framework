package com.mack.rpc.transport.socket.server;

import com.mack.rpc.handler.RequestHandler;
import com.mack.rpc.provider.ServiceProviderImpl;
import com.mack.rpc.registry.NacosServiceRegistry;
import com.mack.rpc.serializer.CommonSerializer;
import com.mack.rpc.transport.socket.AbstractRpcServer;
import mack.factory.ThreadPoolFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

@Component
public class SocketServer extends AbstractRpcServer {

    @Resource
    private ExecutorService threadPool;

    private final RequestHandler requestHandler = new RequestHandler();

    public void registerService() {
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
        scanServices();
    }

    @Bean
    public ExecutorService serviceTask() {
        return ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
    }

    @Override
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(host, port));
            logger.info("服务器启动……");
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("消费者连接: {}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new SocketRequestHandlerThread(socket, requestHandler, CommonSerializer.getByCode(CommonSerializer.KRYO_SERIALIZER)));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("服务器启动时有错误发生:", e);
        }
    }

}
