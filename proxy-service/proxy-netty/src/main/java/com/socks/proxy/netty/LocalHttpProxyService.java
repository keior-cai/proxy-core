package com.socks.proxy.netty;

import com.socks.proxy.netty.proxy.HttpProxy;
import com.socks.proxy.protocol.handshake.HttpHandshakeProtocolHandler;
import com.socks.proxy.protocol.factory.LocalConnectServerFactory;

/**
 * @author: chuangjie
 * @date: 2023/6/4
 **/
public class LocalHttpProxyService extends AbstractNettyTcpService{
    public LocalHttpProxyService(int port, LocalConnectServerFactory connectFactory){
        super(port, new LocalProxyCode(new HttpHandshakeProtocolHandler(), new HttpProxy(connectFactory)));
    }
}
