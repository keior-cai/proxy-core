package com.socks.proxy.netty.local;

import com.socks.proxy.netty.AbstractNettyTcpService;
import com.socks.proxy.netty.proxy.ComplexProxy;
import com.socks.proxy.protocol.factory.LocalConnectServerFactory;
import com.socks.proxy.protocol.handshake.ComplexHandshakeProtocolHandler;
import com.socks.proxy.protocol.listener.LocalConnectListener;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * ss-local混合协议代理服务
 *
 * @author: chuangjie
 * @date: 2023/6/29
 **/
public class LocalComplexProxyService extends AbstractNettyTcpService{

    public LocalComplexProxyService(int port, LocalConnectServerFactory connectFactory,
                                    List<LocalConnectListener> listeners, ExecutorService executor){
        super(port, new LocalProxyCode(new ComplexHandshakeProtocolHandler(),
                new ComplexProxy(connectFactory, listeners, executor)));

    }
}
