package com.socks.proxy.handshake.inbound;

import com.socks.proxy.handshake.NettyServerMiddleProxyFactory;
import com.socks.proxy.protocol.ServerMiddleProxy;
import com.socks.proxy.protocol.listener.ServerMiddleMessageListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@ChannelHandler.Sharable
public class WebsocketTextInboundHandle extends SimpleChannelInboundHandler<TextWebSocketFrame>{

    private final ServerMiddleMessageListener messageListener;

    private final NettyServerMiddleProxyFactory factory;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg){
        String text = msg.text();
        ServerMiddleProxy proxy = factory.getProxy(ctx);
        try {
            messageListener.onText(proxy, text);
        } catch (Throwable cause) {
            messageListener.onCallbackError(proxy, cause);
        }
    }
}
