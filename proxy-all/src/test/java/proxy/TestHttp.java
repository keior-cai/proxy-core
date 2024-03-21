package proxy;

import com.alibaba.fastjson2.JSON;
import com.socks.proxy.handshake.WebsocketProxyConnectFactory;
import com.socks.proxy.local.Local;
import com.socks.proxy.netty.LocalHttpManagerBuilder;
import com.socks.proxy.netty.LocalServiceBuilder;
import com.socks.proxy.netty.NettyTcpService;
import com.socks.proxy.netty.http.HttpHandle;
import com.socks.proxy.netty.http.HttpService;
import com.socks.proxy.netty.local.LocalProxyCode;
import com.socks.proxy.netty.proxy.ProtocolChannelHandler;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.enums.Protocol;
import com.socks.proxy.protocol.exception.LifecycleException;
import com.socks.proxy.protocol.factory.ProxyFactory;
import com.socks.proxy.protocol.handshake.MapConnectContextManager;
import com.socks.proxy.protocol.handshake.handler.LocalProxyMessageHandler;
import com.socks.proxy.protocol.handshake.handler.ProxyContext;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@Slf4j
public class TestHttp{

    public static void main(String[] args) throws Exception{
        MapConnectContextManager manager = new MapConnectContextManager();
        LocalProxyMessageHandler handler = new LocalProxyMessageHandler(manager);
        Protocol protocol = Protocol.COMPLEX;
        ProtocolChannelHandler protocolHandle = null;
        switch(protocol) {
            case HTTP:
            case HTTPS:
                protocolHandle = LocalProxyCode.ofHttp(handler);
                break;
            case SOCKS5:
                protocolHandle = LocalProxyCode.ofSocks5(handler);
                break;
            case COMPLEX:
                protocolHandle = LocalProxyCode.ofComplex(handler);
                break;
        }
        WebsocketProxyConnectFactory connectFactory = WebsocketProxyConnectFactory.createDefault(
                "ws://127.0.0.1:8083");

        //        WebsocketProxyConnectFactory connectFactory = WebsocketProxyConnectFactory.createDefault(
        //                "ws://chuangjie.icu:8042");
        protocolHandle.setFactory(connectFactory);
        TcpService tcpService = new LocalServiceBuilder().setPort(1089).setManager(manager)
                .setProtocolHandle(protocolHandle).builder();

        new Thread(()->{
            try {
                tcpService.start();
            } catch (LifecycleException e) {
                throw new RuntimeException(e);
            }
        }).start();
        Map<String, ProxyFactory> map = new HashMap<>();
        map.put("🇸🇬", connectFactory);
        new LocalHttpManagerBuilder()
                .setPort(8097)
                .setTcpService(tcpService)
                .setProxyFactoryMap(map)
                .setManager(manager)
                .builder().start();
    }
}
