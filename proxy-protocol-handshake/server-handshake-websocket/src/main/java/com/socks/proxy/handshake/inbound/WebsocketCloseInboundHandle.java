package com.socks.proxy.handshake.inbound;

import com.socks.proxy.handshake.connect.WebsocketProxyChannel;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
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

    private final ProxyMessageHandler handler;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloseWebSocketFrame msg){
        handler.handleLocalClose(new WebsocketProxyChannel(ctx.channel()), msg.reasonText() );
    }

}
