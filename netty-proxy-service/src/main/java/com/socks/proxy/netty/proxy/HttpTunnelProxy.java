package com.socks.proxy.netty.proxy;

import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.enums.Protocol;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * resolve http proxy implement
 *
 * @author: chuangjie
 * @date: 2022/11/20
 **/
@Slf4j
@ChannelHandler.Sharable
public class HttpTunnelProxy extends AbstractProxy<FullHttpRequest>{

    public HttpTunnelProxy(ProxyMessageHandler handler){
        super(handler, false);
    }


    @Override
    protected TargetServer resolveRemoteServer(FullHttpRequest msg){
        return new HttpTargetServer(msg);
    }


    @Override
    protected void writeSuccess(ChannelHandlerContext context, FullHttpRequest msg, TargetServer target){
        HttpResponse response = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK,
                Unpooled.buffer());
        context.writeAndFlush(response).addListener(future->{
            if(future.isSuccess()){
                context.pipeline().remove(HttpServerCodec.class);
                context.pipeline().remove(HttpObjectAggregator.class);
            }
        });
    }


    @Override
    protected void writeSuccessAfter(ChannelHandlerContext ctx, FullHttpRequest msg, TargetServer target,
                                     ProxyConnect proxyConnect){
        if(Objects.equals(target.sourceProtocol(), Protocol.HTTP)){
            ctx.writeAndFlush(msg.content().copy());
        }
    }


    @Override
    protected void writeFail(ChannelHandlerContext context, FullHttpRequest msg, TargetServer target){
        context.writeAndFlush(new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.NOT_FOUND));
    }

}
