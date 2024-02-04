package com.socks.proxy.netty.proxy;

import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandResponse;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class Socks5CommandHandler extends AbstractProxy<Socks5CommandRequest>{

    public Socks5CommandHandler(ProxyMessageHandler handler){
        super(handler);
    }


    @Override
    protected TargetServer resolveRemoteServer(Socks5CommandRequest msg){
        return new Socks5TargetServer(msg);
    }


    @Override
    protected void writeSuccess(ChannelHandlerContext context, Socks5CommandRequest msg, TargetServer target){
        context.writeAndFlush(new DefaultSocks5CommandResponse(Socks5CommandStatus.SUCCESS, msg.dstAddrType()));

    }


    @Override
    protected void writeFail(ChannelHandlerContext context, Socks5CommandRequest msg, TargetServer target){
        context.writeAndFlush(new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, msg.dstAddrType()));
    }
}
