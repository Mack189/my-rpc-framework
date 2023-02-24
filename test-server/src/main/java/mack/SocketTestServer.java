package mack;

import com.mack.rpc.annotation.ServiceScan;
import com.mack.rpc.transport.socket.server.SocketServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@ServiceScan
public class SocketTestServer {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SocketServer.class);
        SocketServer socketServer = (SocketServer) applicationContext.getBean("socketServer");
        socketServer.registerService();
        socketServer.start();
    }

}
