package com.socks.proxy.netty.proxy;

import com.socks.proxy.handshake.connect.DirectConnectChannel;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.handshake.handler.LocalProxyMessageHandler;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.logging.ByteBufFormat;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 代理处理器
 *
 * @author: chuangjie
 * @date: 2023/5/26
 **/
@Slf4j
@AllArgsConstructor
public abstract class AbstractProxy<I> extends SimpleChannelInboundHandler<I>{

    private final ProxyMessageHandler handler;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, I msg){
        log.debug("receive {} handshake", this.getClass().getSimpleName());
        TargetServer target = resolveRemoteServer(msg);
        ChannelPipeline pipeline = ctx.pipeline().addFirst(new LoggingHandler(LogLevel.DEBUG, ByteBufFormat.HEX_DUMP));
        try {
            handler.targetConnect(new DirectConnectChannel(ctx.channel()), target);
            pipeline.addLast(new ReadLocalInboundHandler(handler)).remove(this);
            writeSuccess(ctx, msg, target);
        } catch (Exception e) {
            writeFail(ctx, msg, target);
        }
    }


    @AllArgsConstructor
    private static class ReadLocalInboundHandler extends SimpleChannelInboundHandler<ByteBuf>{

        private final ProxyMessageHandler connect;


        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg){
            connect.handleLocalBinaryMessage(new DirectConnectChannel(ctx.channel()), ByteBufUtil.getBytes(msg));
        }
    }


    /**
     * 解决socket消息生成目标地址连接信息
     *
     * @param msg socket 消息对象
     * @return 目标地址连接信息
     */
    protected abstract TargetServer resolveRemoteServer(I msg);


    /**
     * 通知请求服务，代理连接准备就绪
     */
    protected abstract void writeSuccess(ChannelHandlerContext context, I msg, TargetServer target);


    /**
     * 通知请求服务，代理连接连接失败
     */
    protected abstract void writeFail(ChannelHandlerContext context, I msg, TargetServer target);

}
