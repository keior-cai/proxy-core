package com.socks.proxy.handshake.handler;

import com.socks.proxy.handshake.connect.DirectConnectChannel;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 转发本地客户端数据到目标服务上
 */
@Slf4j
@AllArgsConstructor
public class ForwardBinaryData extends SimpleChannelInboundHandler<ByteBuf>{

    private final ProxyMessageHandler handler;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg){
        if(log.isDebugEnabled()){
            log.debug("receive target server binary data");
        }
        handler.handleTargetBinaryMessage(new DirectConnectChannel(ctx.channel()), ByteBufUtil.getBytes(msg));
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        handler.handleTargetClose(new DirectConnectChannel(ctx.channel()), new Exception("目标服务断开连接"));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        handler.handleTargetClose(new DirectConnectChannel(ctx.channel()),
                new Exception("远程连接发生异常:" + cause.getMessage()));
    }
}
