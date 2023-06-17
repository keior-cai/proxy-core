package com.socks.proxy.handshake.inbound;

import com.socks.proxy.handshake.NettyServerMiddleProxyFactory;
import com.socks.proxy.protocol.listener.ServerMiddleMessageListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketCloseStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: chuangjie
 * @date: 2023/5/21
 **/
@Slf4j
@AllArgsConstructor
@ChannelHandler.Sharable
public class WebsocketCloseInboundHandle extends SimpleChannelInboundHandler<CloseWebSocketFrame>{

    private final ServerMiddleMessageListener messageListener;

    private final NettyServerMiddleProxyFactory factory;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloseWebSocketFrame msg){
        messageListener.onClose(factory.getProxy(ctx), msg.statusCode(), msg.reasonText());
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        messageListener.onClose(factory.getProxy(ctx), WebSocketCloseStatus.NORMAL_CLOSURE.code(),
                WebSocketCloseStatus.INTERNAL_SERVER_ERROR.reasonText());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        messageListener.onError(factory.getProxy(ctx), cause);
    }
}
