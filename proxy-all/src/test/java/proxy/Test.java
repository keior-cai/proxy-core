package proxy;

import com.socks.proxy.handshake.WebsocketProxyConnectFactory;
import com.socks.proxy.local.Local;
import com.socks.proxy.netty.LocalServiceBuilder;
import com.socks.proxy.netty.local.LocalProxyCode;
import com.socks.proxy.netty.proxy.ProtocolChannelHandler;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.enums.Protocol;
import com.socks.proxy.protocol.handshake.MapConnectContextManager;
import com.socks.proxy.protocol.handshake.handler.LocalProxyMessageHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@Slf4j
public class Test{


    public static void main(String[] args) throws Exception{
        Local.start(new MapConnectContextManager());
    }
}
