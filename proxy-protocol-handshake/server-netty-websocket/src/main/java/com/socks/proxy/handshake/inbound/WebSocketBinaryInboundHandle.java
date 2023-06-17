package com.socks.proxy.handshake.inbound;

import com.socks.proxy.handshake.NettyServerMiddleProxyFactory;
import com.socks.proxy.handshake.constant.WebsocketAttrConstant;
import com.socks.proxy.protocol.ICipher;
import com.socks.proxy.protocol.ServerMiddleProxy;
import com.socks.proxy.protocol.listener.ServerMiddleMessageListener;
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

    private final ServerMiddleMessageListener messageListener;

    private final NettyServerMiddleProxyFactory factory;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame msg){
        ServerMiddleProxy proxy = factory.getProxy(ctx);
        byte[] bytes = ByteBufUtil.getBytes(msg.content());
        ICipher iCipher = ctx.channel().attr(WebsocketAttrConstant.CIPHER).get();
        try {
            messageListener.onBinary(proxy, iCipher.decode(bytes));
        } catch (Throwable cause) {
            messageListener.onCallbackError(proxy, cause);
        }
    }

}
