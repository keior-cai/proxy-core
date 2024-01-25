package com.socks.proxy.netty.proxy;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.SocksVersion;
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialResponse;
import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * socks5 代理处理器
 */
@Slf4j
@AllArgsConstructor
@ChannelHandler.Sharable
public final class Socks5Proxy extends SimpleChannelInboundHandler<Socks5InitialRequest>{

    private AbstractProxy<?> proxyHandle;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Socks5InitialRequest msg){
        ChannelPipeline pipeline = ctx.pipeline();
        if(msg.version() != SocksVersion.SOCKS5){
            return;
        }
        pipeline.addFirst(new Socks5CommandRequestDecoder()).addLast(proxyHandle);
        pipeline.remove(this);
        ctx.writeAndFlush(new DefaultSocks5InitialResponse(Socks5AuthMethod.NO_AUTH));
    }

}
