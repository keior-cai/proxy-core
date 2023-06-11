package com.socks.proxy.netty.proxy;

import com.socks.proxy.netty.connect.DefaultSocks5NettyConnect;
import com.socks.proxy.netty.constant.AttrConstant;
import com.socks.proxy.protocol.DefaultDstServer;
import com.socks.proxy.protocol.DstServer;
import com.socks.proxy.protocol.LocalProxyConnect;
import com.socks.proxy.protocol.factory.LocalConnectServerFactory;
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

    private final LocalConnectServerFactory connectFactory;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Socks5InitialRequest msg){
        ChannelPipeline pipeline = ctx.pipeline();
        if(msg.version() != SocksVersion.SOCKS5){
            ctx.close();
            return;
        }
        pipeline.addFirst(new Socks5CommandRequestDecoder()).addLast(new Socks5CommandHandler(connectFactory))
                .remove(this);
        ctx.writeAndFlush(new DefaultSocks5InitialResponse(Socks5AuthMethod.NO_AUTH));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        log.error("", cause);
    }


    private static class Socks5CommandHandler extends AbstractProxy<Socks5CommandRequest>{

        public Socks5CommandHandler(LocalConnectServerFactory connectFactory){
            super(connectFactory);
        }


        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Socks5CommandRequest msg){
            log.debug("receive socks5 handshake");
            ctx.channel().attr(AttrConstant.SOCKS5_ADDRESS_TYPE).set(msg.dstAddrType());
            super.channelRead0(ctx, msg);
        }


        @Override
        protected DstServer resolveRemoteServer(Socks5CommandRequest msg){
            return new DefaultDstServer(msg.dstAddr(), msg.dstPort());
        }


        @Override
        protected LocalProxyConnect createProxyConnect(ChannelHandlerContext ctx, DstServer dstServer){
            return new DefaultSocks5NettyConnect(ctx);
        }
    }
}
