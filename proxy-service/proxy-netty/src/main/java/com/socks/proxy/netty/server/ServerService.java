package com.socks.proxy.netty.server;

import com.socks.proxy.netty.AbstractNettyTcpService;
import io.netty.channel.ChannelHandler;

/**
 * @author: chuangjie
 * @date: 2023/6/4
 **/
public class ServerService extends AbstractNettyTcpService{

    public ServerService(int port, ChannelHandler handler){
        super(port, handler);
    }

}
