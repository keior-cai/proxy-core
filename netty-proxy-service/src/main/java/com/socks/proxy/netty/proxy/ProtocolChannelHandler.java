package com.socks.proxy.netty.proxy;

import com.socks.proxy.protocol.factory.ProxyFactory;
import io.netty.channel.ChannelHandler;

public interface ProtocolChannelHandler extends ChannelHandler{

    void setFactory(ProxyFactory factory);
}
