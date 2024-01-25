package com.socks.proxy.netty.local;

import com.socks.proxy.netty.proxy.Socks5CommandHandler;
import com.socks.proxy.netty.proxy.Socks5Proxy;
import com.socks.proxy.protocol.handshake.Socks5HandshakeProtocolHandler;
import com.socks.proxy.protocol.handshake.handler.AbstractLocalProxyMessageHandler;

/**
 * socks5 ss-local代理服务
 *
 * @author: chuangjie
 * @date: 2023/6/4
 **/
public class LocalSocks5ProxyService extends AbstractLocalProxyService{

    public LocalSocks5ProxyService(int port, AbstractLocalProxyMessageHandler messageHandler){
        super(port, new LocalProxyCode(new Socks5HandshakeProtocolHandler(),
                new Socks5Proxy(new Socks5CommandHandler(messageHandler)), messageHandler));
    }

}
