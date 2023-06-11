package com.socks.proxy.handshake.inbound;

import com.socks.proxy.handshake.MessageListener;
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

    private final MessageListener messageListener;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg){
        String text = msg.text();
        try {
            messageListener.onText(ctx, text);
        } catch (Throwable cause) {
            messageListener.onCallbackError(ctx, cause);
        }
    }
}
