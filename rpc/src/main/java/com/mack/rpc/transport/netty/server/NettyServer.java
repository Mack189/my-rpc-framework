package com.mack.rpc.transport.netty.server;

import com.mack.rpc.provider.ServiceProviderImpl;
import com.mack.rpc.registry.NacosServiceRegistry;
import com.mack.rpc.serializer.CommonSerializer;
import com.mack.rpc.transport.netty.codec.CommonDecoder;
import com.mack.rpc.transport.netty.codec.CommonEncoder;
import com.mack.rpc.transport.netty.handler.NettyServerHandler;
import com.mack.rpc.transport.socket.AbstractRpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * NIO方式服务提供
 */
@AllArgsConstructor
@Getter
@Setter
@Builder
@Component
public class NettyServer extends AbstractRpcServer {

    private static final String host = "127.0.0.1";

    private static final int port = 9999;

    public void registerService() {
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
        scanServices();
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
                                    .addLast(new CommonEncoder(CommonSerializer.getByCode(CommonSerializer.KRYO_SERIALIZER)))
                                    .addLast(new CommonDecoder())
                                    .addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(host, port).sync();
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
