package com.socks.proxy.handshake.inbound;

import com.socks.proxy.handshake.connect.WebsocketProxyChannel;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
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

    private final ProxyMessageHandler handler;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame msg){
        if(log.isDebugEnabled()){
            log.debug("receive local binary data");
        }
        handler.handleLocalBinaryMessage(new WebsocketProxyChannel(ctx.channel()), ByteBufUtil.getBytes(msg.content()));
    }

}
