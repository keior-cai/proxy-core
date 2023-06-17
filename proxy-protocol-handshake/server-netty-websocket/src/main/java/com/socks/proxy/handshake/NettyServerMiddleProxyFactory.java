package com.socks.proxy.handshake;

import com.socks.proxy.protocol.ServerMiddleProxy;
import io.netty.channel.ChannelHandlerContext;

public interface NettyServerMiddleProxyFactory{

    ServerMiddleProxy getProxy(ChannelHandlerContext context);
}
