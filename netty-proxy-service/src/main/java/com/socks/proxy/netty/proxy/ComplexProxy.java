package com.socks.proxy.netty.proxy;

import com.socks.proxy.protocol.factory.ProxyFactory;
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
public class ComplexProxy extends SimpleChannelInboundHandler<Object> implements ProtocolChannelHandler{

    private final List<ProtocolChannelHandler> listeners;


    public ComplexProxy(List<ProtocolChannelHandler> listeners){
        this.listeners = listeners;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg){
        ChannelPipeline pipeline = ctx.pipeline();
        for(ProtocolChannelHandler handler : listeners) {
            pipeline.addLast(handler);
        }
        pipeline.remove(this);
        ctx.fireChannelRead(msg);
    }


    @Override
    public void setFactory(ProxyFactory factory){
        for(ProtocolChannelHandler handler : listeners){
            handler.setFactory(factory);
        }
    }
}
