package com.socks.proxy.handshake;

import com.socks.proxy.protocol.ServerMiddleProxy;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.codes.ProxyMessage;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;

/**
 * @author: chuangjie
 * @date: 2023/6/17
 **/
@AllArgsConstructor
public class DefaultServerMiddleProxyFactory implements NettyServerMiddleProxyFactory{

    private final ProxyCodes<? extends ProxyMessage> codes;


    @Override
    public ServerMiddleProxy getProxy(ChannelHandlerContext context){
        return new NettyServerWebsocketMiddleProxy(context, codes);
    }
}
