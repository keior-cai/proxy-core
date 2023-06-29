package com.socks.proxy.netty.proxy;

import com.socks.proxy.protocol.factory.LocalConnectServerFactory;
import com.socks.proxy.protocol.listener.LocalConnectListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author: chuangjie
 * @date: 2023/6/29
 **/
@ChannelHandler.Sharable
public class ComplexProxy extends SimpleChannelInboundHandler<Object>{

    private final LocalConnectServerFactory  factory;
    private final List<LocalConnectListener> listeners;
    private final ExecutorService            executor;


    public ComplexProxy(LocalConnectServerFactory factory, List<LocalConnectListener> listeners,
                        ExecutorService executor){
        this.factory = factory;
        this.listeners = listeners;
        this.executor = executor;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg){
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast(new Socks5Proxy(factory, listeners, executor))
                .addLast(new HttpTunnelProxy(factory, listeners, executor));
        ctx.fireChannelRead(msg);
        pipeline.remove(this);
    }
}
