package com.socks.proxy.netty.local;

import com.socks.proxy.handshake.connect.DirectConnectChannel;
import com.socks.proxy.protocol.enums.Protocol;
import com.socks.proxy.protocol.handshake.HandshakeProtocolHandler;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.socksx.SocksPortUnificationServerHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;

@Slf4j
@AllArgsConstructor
@ChannelHandler.Sharable
public class LocalProxyCode extends SimpleChannelInboundHandler<ByteBuf>{

    private final HandshakeProtocolHandler protocolHandler;

    private final ChannelHandler handler;

    private final ProxyMessageHandler messageHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws IOException{
        messageHandler.handlerShakeEvent(new DirectConnectChannel(ctx.channel()), Collections.emptyMap());
        Protocol protocol = protocolHandler.handler(new ByteArrayInputStream(ByteBufUtil.getBytes(msg)));
        ChannelPipeline pipeline = ctx.pipeline();
        switch(protocol) {
            case HTTP:
            case HTTPS:
                pipeline.addLast(new HttpServerCodec())
                        .addLast(new HttpObjectAggregator(65536));
                break;
            case SOCKS5:
                pipeline.addLast(new SocksPortUnificationServerHandler());
                break;
            case SOCKS4:
                break;
        }
        pipeline.addLast(handler);
        pipeline.remove(this);
        ctx.fireChannelRead(msg.resetReaderIndex().retain());
    }
}
