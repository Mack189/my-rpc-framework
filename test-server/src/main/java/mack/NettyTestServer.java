package mack;

import com.mack.rpc.annotation.ServiceScan;
import com.mack.rpc.transport.netty.server.NettyServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@ServiceScan
public class NettyTestServer {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServer.class);
        NettyServer nettyServer = (NettyServer) applicationContext.getBean("nettyServer");
        nettyServer.registerService();
        nettyServer.start();
    }

}

