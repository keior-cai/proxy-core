package proxy;

import com.socks.proxy.handshake.WebsocketHandler;
import com.socks.proxy.netty.ServerServiceBuilder;
import com.socks.proxy.netty.connect.DirectConnectFactory;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.handshake.ConnectContextManager;
import com.socks.proxy.protocol.handshake.MapConnectContextManager;
import com.socks.proxy.protocol.handshake.handler.ServiceProxyMessageHandler;

/**
 * @author: chuangjie
 * @date: 2023/6/10
 **/
public class TestServer{
    public static void main(String[] args) throws Exception{
        ConnectContextManager manager = new MapConnectContextManager();
        WebsocketHandler websocketHandler = new WebsocketHandler(
                ()->new ServiceProxyMessageHandler(manager, DirectConnectFactory.INSTANCE));
        TcpService service = new ServerServiceBuilder()
                .setHandler(websocketHandler)
                .setPort(8083).builder();
        service.start();
        System.out.println("启动成功");
    }
}
