package com.socks.proxy.netty.proxy;

import com.socks.proxy.netty.connect.DefaultSocks5NettyConnect;
import com.socks.proxy.netty.constant.AttrConstant;
import com.socks.proxy.protocol.DefaultTargetServer;
import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.factory.LocalConnectServerFactory;
import com.socks.proxy.protocol.listener.LocalConnectListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Slf4j
@ChannelHandler.Sharable
public class Socks5CommandHandler extends AbstractProxy<Socks5CommandRequest>{

    public Socks5CommandHandler(LocalConnectServerFactory connectFactory, List<LocalConnectListener> listeners,
                                ExecutorService executor){
        super(connectFactory, listeners, executor);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Socks5CommandRequest msg){
        log.debug("receive socks5 handshake");
        ctx.channel().attr(AttrConstant.SOCKS5_ADDRESS_TYPE).set(msg.dstAddrType());
        super.channelRead0(ctx, msg);
    }


    @Override
    protected TargetServer resolveRemoteServer(Socks5CommandRequest msg){
        return new DefaultTargetServer(msg.dstAddr(), msg.dstPort());
    }


    @Override
    protected LocalConnect createProxyConnect(ChannelHandlerContext ctx, TargetServer dstServer,
                                              List<LocalConnectListener> listeners){
        return new DefaultSocks5NettyConnect(ctx, dstServer, listeners);

    }
}
