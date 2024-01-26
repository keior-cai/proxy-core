package com.socks.proxy.netty.local;

import com.socks.proxy.netty.AbstractNettyTcpService;
import com.socks.proxy.netty.proxy.HttpTunnelProxy;
import com.socks.proxy.protocol.handshake.HttpHandshakeProtocolHandler;
import com.socks.proxy.protocol.handshake.handler.AbstractLocalProxyMessageHandler;

/**
 * ss-local http代理服务
 *
 * @author: chuangjie
 * @date: 2023/6/4
 **/
public class LocalHttpProxyService extends AbstractNettyTcpService{
    public LocalHttpProxyService(int port, AbstractLocalProxyMessageHandler proxyHandler){
        super(port, new LocalProxyCode(new HttpHandshakeProtocolHandler(), new HttpTunnelProxy(proxyHandler),
                proxyHandler));
    }
}
