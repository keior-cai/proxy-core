package com.socks.proxy.netty.proxy;

import com.socks.proxy.handshake.connect.DirectConnectChannel;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.connect.ProxyConnect;
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

import java.util.concurrent.ExecutorService;

/**
 * 代理处理器
 *
 * @author: chuangjie
 * @date: 2023/5/26
 **/
@Slf4j
public abstract class AbstractProxy<I> extends SimpleChannelInboundHandler<I>{

    private final ProxyMessageHandler handler;

    private final ExecutorService executorService;



    public AbstractProxy(ProxyMessageHandler handler, ExecutorService executorService){
        this(handler, executorService,true);
    }


    public AbstractProxy(ProxyMessageHandler handler,ExecutorService executorService, boolean autoRelease){
        super(autoRelease);
        this.handler = handler;
        this.executorService = executorService;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, I msg){
        executorService.execute(()->{
            TargetServer target = resolveRemoteServer(msg);
            ChannelPipeline pipeline = ctx.pipeline().addFirst(new LoggingHandler(LogLevel.DEBUG, ByteBufFormat.HEX_DUMP));
            try {
                ProxyConnect localConnect = new DirectConnectChannel(ctx.channel());
                handler.targetConnect(localConnect, target);
                pipeline.addLast(new ReadLocalInboundHandler(handler)).remove(this);
                writeSuccess(ctx, msg, target,handler);
            } catch (Exception e) {
                writeFail(ctx, msg, target);
                handler.handleLocalClose(new DirectConnectChannel(ctx.channel()), e);
            }
        });

    }



    @AllArgsConstructor
    private static class ReadLocalInboundHandler extends SimpleChannelInboundHandler<ByteBuf>{

        private final ProxyMessageHandler connect;


        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg){
            connect.handleLocalBinaryMessage(new DirectConnectChannel(ctx.channel()), ByteBufUtil.getBytes(msg));
        }


        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            connect.handleLocalClose(new DirectConnectChannel(ctx.channel()), new Exception("本地客户端断开连接"));
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
    protected abstract void writeSuccess(ChannelHandlerContext context, I msg, TargetServer target,
                                         ProxyMessageHandler handler);


    /**
     * 通知请求服务，代理连接连接失败
     */
    protected abstract void writeFail(ChannelHandlerContext context, I msg, TargetServer target);

}
