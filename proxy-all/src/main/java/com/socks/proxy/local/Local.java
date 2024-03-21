package com.socks.proxy.local;

import com.socks.proxy.handshake.WebsocketProxyConnectFactory;
import com.socks.proxy.netty.LocalServiceBuilder;
import com.socks.proxy.netty.local.LocalProxyCode;
import com.socks.proxy.netty.proxy.ProtocolChannelHandler;
import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.enums.Protocol;
import com.socks.proxy.protocol.exception.LifecycleException;
import com.socks.proxy.protocol.handshake.MapConnectContextManager;
import com.socks.proxy.protocol.handshake.handler.LocalProxyMessageHandler;

/**
 * @author: chuangjie
 * @date: 2024/3/20
 **/
public class Local{

    public static void start(MapConnectContextManager manager){
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
        try {
            tcpService.start();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
    }
}
