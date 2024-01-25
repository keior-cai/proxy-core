package com.socks.proxy.netty.local;

import com.socks.proxy.netty.AbstractNettyTcpService;
import io.netty.channel.ChannelHandler;

/**
 * @author: chuangjie
 * @date: 2024/1/25
 **/
public abstract class AbstractLocalProxyService extends AbstractNettyTcpService{

    public AbstractLocalProxyService(int port, ChannelHandler handler){
        super(port, handler);
    }
}
