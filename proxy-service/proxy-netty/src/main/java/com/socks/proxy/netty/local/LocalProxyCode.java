package com.socks.proxy.netty.local;

import com.socks.proxy.protocol.handshake.HandshakeProtocolHandler;
import com.socks.proxy.protocol.enums.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.socksx.SocksPortUnificationServerHandler;
import lombok.AllArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@AllArgsConstructor
@ChannelHandler.Sharable
public class LocalProxyCode extends SimpleChannelInboundHandler<ByteBuf>{

    private final HandshakeProtocolHandler protocolHandler;

    private final ChannelHandler handler;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws IOException{
        Protocol protocol = protocolHandler.handler(new ByteArrayInputStream(ByteBufUtil.getBytes(msg)));
        ChannelPipeline pipeline = ctx.pipeline();
        switch(protocol) {
            case HTTP:
            case HTTPS:
                pipeline.addLast(new HttpServerCodec());
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
