package com.socks.proxy.handshake.inbound;

import com.socks.proxy.handshake.connect.WebsocketProxyChannel;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
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

    private final ProxyMessageHandler handler;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg){
        handler.handleLocalTextMessage(new WebsocketProxyChannel(ctx.channel()), msg.text());
    }
}
