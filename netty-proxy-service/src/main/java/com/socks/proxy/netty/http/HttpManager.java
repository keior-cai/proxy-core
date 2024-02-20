package com.socks.proxy.netty.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author: chuangjie
 * @date: 2024/2/20
 **/
public class HttpManager extends SimpleChannelInboundHandler<FullHttpRequest>{
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest)
            throws Exception{

    }

}
