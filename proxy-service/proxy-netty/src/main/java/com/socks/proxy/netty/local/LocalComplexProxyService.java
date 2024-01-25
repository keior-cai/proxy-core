package com.socks.proxy.netty.local;

import com.socks.proxy.netty.proxy.ComplexProxy;
import com.socks.proxy.netty.proxy.HttpTunnelProxy;
import com.socks.proxy.netty.proxy.Socks5CommandHandler;
import com.socks.proxy.netty.proxy.Socks5Proxy;
import com.socks.proxy.protocol.handshake.ComplexHandshakeProtocolHandler;
import com.socks.proxy.protocol.handshake.handler.AbstractLocalProxyMessageHandler;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Arrays;
import java.util.List;

/**
 * ss-local混合协议代理服务
 *
 * @author: chuangjie
 * @date: 2023/6/29
 **/
public class LocalComplexProxyService extends AbstractLocalProxyService{

    public LocalComplexProxyService(int port, AbstractLocalProxyMessageHandler messageHandler){
        super(port, of(messageHandler));
    }


    private static LocalProxyCode of(AbstractLocalProxyMessageHandler messageHandler){
        ComplexHandshakeProtocolHandler protocolHandler = new ComplexHandshakeProtocolHandler();
        List<SimpleChannelInboundHandler<?>> list = Arrays.asList(
                new Socks5Proxy(new Socks5CommandHandler(messageHandler)), new HttpTunnelProxy(messageHandler));
        return new LocalProxyCode(protocolHandler, new ComplexProxy(list),messageHandler);
    }
}
