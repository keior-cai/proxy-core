package com.socks.proxy.handshake.inbound;

import com.socks.proxy.handshake.MessageListener;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
@AllArgsConstructor
public class WebSocketBinaryInboundHandle extends SimpleChannelInboundHandler<BinaryWebSocketFrame>{

    private final MessageListener messageListener;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame msg){
        try {
            messageListener.onBinary(ctx, ByteBufUtil.getBytes(msg.content()));
        } catch (Throwable cause) {
            messageListener.onCallbackError(ctx, cause);
        }
    }

}
