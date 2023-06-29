package com.socks.proxy.netty.local;

import com.socks.proxy.netty.AbstractNettyTcpService;
import com.socks.proxy.netty.proxy.HttpTunnelProxy;
import com.socks.proxy.protocol.factory.LocalConnectServerFactory;
import com.socks.proxy.protocol.handshake.HttpHandshakeProtocolHandler;
import com.socks.proxy.protocol.listener.LocalConnectListener;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * ss-local http代理服务
 *
 * @author: chuangjie
 * @date: 2023/6/4
 **/
public class LocalHttpProxyService extends AbstractNettyTcpService{
    public LocalHttpProxyService(int port, LocalConnectServerFactory connectFactory,
                                 List<LocalConnectListener> listeners, ExecutorService executor){
        super(port, new LocalProxyCode(new HttpHandshakeProtocolHandler(),
                new HttpTunnelProxy(connectFactory, listeners, executor)));
    }
}
