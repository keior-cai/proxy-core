package com.socks.proxy.netty.local;

import com.socks.proxy.handshake.connect.DirectConnectChannel;
import com.socks.proxy.handshake.constant.AttrConstant;
import com.socks.proxy.netty.AbstractNettyTcpService;
import com.socks.proxy.protocol.handshake.handler.AbstractLocalProxyMessageHandler;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Collections;
import java.util.UUID;

/**
 * @author: chuangjie
 * @date: 2024/1/25
 **/
public abstract class AbstractLocalProxyService extends AbstractNettyTcpService{



    public AbstractLocalProxyService(int port, ChannelHandler handler){
        super(port, handler);
    }
}
