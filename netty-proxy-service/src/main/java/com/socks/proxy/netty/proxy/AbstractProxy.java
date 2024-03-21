package com.socks.proxy.netty.proxy;

import com.socks.proxy.handshake.connect.DirectConnectChannel;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.connect.ConnectProxyConnect;
import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.factory.ProxyFactory;
import com.socks.proxy.protocol.handshake.handler.ProxyContext;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * 代理处理器
 *
 * @author: chuangjie
 * @date: 2023/5/26
 **/
@Slf4j
public abstract class AbstractProxy<I> extends SimpleChannelInboundHandler<I> implements ProtocolChannelHandler{

    private final ProxyMessageHandler handler;

    @Setter
    protected ProxyFactory factory;

    public AbstractProxy(ProxyMessageHandler handler){
        this(handler, true);
    }


    public AbstractProxy(ProxyMessageHandler handler, boolean autoRelease){
        super(autoRelease);
        this.handler = handler;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, I msg){
        TargetServer target = resolveRemoteServer(msg);
        ctx.pipeline().addLast(new ReadLocalInboundHandler(handler)).remove(this);
        try {
            ProxyConnect localConnect = new DirectConnectChannel(ctx.channel());
            ConnectProxyConnect connectProxyConnect = factory.create(target, handler);
            handler.handleDstConnect(localConnect, connectProxyConnect,target);
            connectProxyConnect.connect();
            handler.handlerShakeEvent(localConnect);
            writeSuccess(ctx, msg, target, handler);
        } catch (Exception e) {
            writeFail(ctx, msg, target);
            ctx.close().addListener((ChannelFutureListener) channelFuture->{
                if(channelFuture.isSuccess()){
                    handler.handleLocalClose(new DirectConnectChannel(ctx.channel()), e.getMessage());
                }
            });
        }
    }


    /**
     * 这里暂时先样，这里需要改为写缓冲区，来提高本地服务的性能
     */
    @AllArgsConstructor
    private static class ReadLocalInboundHandler extends SimpleChannelInboundHandler<ByteBuf>{

        private final ProxyMessageHandler connect;


        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg){
            connect.handleLocalBinaryMessage(new DirectConnectChannel(ctx.channel()), ByteBufUtil.getBytes(msg));
        }


        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception{
            ctx.close().addListener((ChannelFutureListener) channelFuture->{
                if(channelFuture.isSuccess()){
                    connect.handleLocalClose(new DirectConnectChannel(ctx.channel()), "本地客户端断开连接");
                }
            });
            super.channelUnregistered(ctx);
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
