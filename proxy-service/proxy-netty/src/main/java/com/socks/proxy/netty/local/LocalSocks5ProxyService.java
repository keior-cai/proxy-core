package com.socks.proxy.netty.local;

import com.socks.proxy.netty.AbstractNettyTcpService;
import com.socks.proxy.netty.proxy.Socks5Proxy;
import com.socks.proxy.protocol.factory.LocalConnectServerFactory;
import com.socks.proxy.protocol.handshake.Socks5HandshakeProtocolHandler;
import com.socks.proxy.protocol.listener.LocalConnectListener;

import java.util.List;

/**
 * @author: chuangjie
 * @date: 2023/6/4
 **/
public class LocalSocks5ProxyService extends AbstractNettyTcpService{

    public LocalSocks5ProxyService(int port, LocalConnectServerFactory connectFactory,
                                   List<LocalConnectListener> listeners){
        super(port,
                new LocalProxyCode(new Socks5HandshakeProtocolHandler(), new Socks5Proxy(connectFactory, listeners)));
    }
}
