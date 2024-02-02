package com.socks.proxy.netty.proxy;

import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.handshake.handler.LocalProxyMessageHandler;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

/**
 * resolve http proxy implement
 *
 * @author: chuangjie
 * @date: 2022/11/20
 **/
@Slf4j
@ChannelHandler.Sharable
public class HttpTunnelProxy extends AbstractProxy<HttpRequest>{

    public HttpTunnelProxy(ProxyMessageHandler handler){
        super(handler);
    }


    @Override
    protected TargetServer resolveRemoteServer(HttpRequest msg){
        return new HttpTargetServer(msg);
    }


    @Override
    protected void writeSuccess(ChannelHandlerContext context, HttpRequest msg, TargetServer target){
        HttpResponse response = new DefaultFullHttpResponse(msg.protocolVersion(),
                HttpResponseStatus.valueOf(200, "Connection established"), Unpooled.buffer());
        context.pipeline().remove(HttpServerCodec.class);
        context.pipeline().remove(HttpObjectAggregator.class);
        context.writeAndFlush(response);
    }


    @Override
    protected void writeFail(ChannelHandlerContext context, HttpRequest msg, TargetServer target){
        context.writeAndFlush(new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.NOT_FOUND));
    }

}
