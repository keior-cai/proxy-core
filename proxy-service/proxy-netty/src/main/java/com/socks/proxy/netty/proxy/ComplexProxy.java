package com.socks.proxy.netty.proxy;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

/**
 * 混合协议处理器
 *
 * @author: chuangjie
 * @date: 2023/6/29
 **/
@ChannelHandler.Sharable
public class ComplexProxy extends SimpleChannelInboundHandler<Object>{

    private final List<SimpleChannelInboundHandler<?>> listeners;


    public ComplexProxy(List<SimpleChannelInboundHandler<?>> listeners){
        this.listeners = listeners;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg){
        ChannelPipeline pipeline = ctx.pipeline();
        for(SimpleChannelInboundHandler<?> handler : listeners) {
            pipeline.addLast(handler);
        }
        ctx.fireChannelRead(msg);
        pipeline.remove(this);
    }
}
