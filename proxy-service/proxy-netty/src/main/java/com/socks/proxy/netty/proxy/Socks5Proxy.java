package com.socks.proxy.netty.proxy;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.SocksVersion;
import io.netty.handler.codec.socksx.v5.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * socks5 代理处理器
 */
@Slf4j
@AllArgsConstructor
@ChannelHandler.Sharable
public final class Socks5Proxy extends SimpleChannelInboundHandler<Socks5InitialRequest>{

    private final AbstractProxy<?> proxy;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Socks5InitialRequest msg){
        ChannelPipeline pipeline = ctx.pipeline();
        if(msg.version() != SocksVersion.SOCKS5){
            ctx.close();
            return;
        }
        pipeline.addFirst(new Socks5CommandRequestDecoder()).addLast(proxy).remove(this);
        ctx.writeAndFlush(new DefaultSocks5InitialResponse(Socks5AuthMethod.NO_AUTH));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        log.error("", cause);
    }

}
