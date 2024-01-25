package com.socks.proxy.handshake.handler;

import com.socks.proxy.handshake.connect.DirectConnectChannel;
import com.socks.proxy.protocol.codes.DefaultProxyCommandCodes;
import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.handshake.handler.ProxyContext;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
import com.socks.proxy.protocol.handshake.handler.AbstractServiceProxyMessageHandler;
import com.socks.proxy.util.RSAUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * netty 实现websocket 代理
 *
 * @author: chuangjie
 * @date: 2024/1/25
 **/

@Slf4j
public class NettyWebsocketProxyMessageHandler extends AbstractServiceProxyMessageHandler{

    private final Bootstrap      bootstrap = new Bootstrap();
    private final EventLoopGroup group;


    public NettyWebsocketProxyMessageHandler(RSAUtil rsaUtil){
        super(rsaUtil, new DefaultProxyCommandCodes<>());
        int processors = Runtime.getRuntime().availableProcessors();
        group = new NioEventLoopGroup(processors * 2);
        init();
        new Thread(()->{
            while(!Thread.currentThread().isInterrupted()) {
                Map<String, ProxyContext> contextMap = NettyWebsocketProxyMessageHandler.this.getContextMap();
                contextMap.forEach((k, v)->{
                    log.info("k = {}, v = {}", k, v);
                });
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }


    private void init(){
        bootstrap.group(group).channel(NioSocketChannel.class).handler(new LoggingHandler(LogLevel.DEBUG));
    }


    @Override
    protected ProxyConnect targetConnect(String host, int port){
        ChannelFuture connect = bootstrap.connect(host, port);
        connect.channel().pipeline().addLast(new ForwardBinaryData(this));
        try {
            connect.sync().get();
            return new DirectConnectChannel(connect.channel());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    @AllArgsConstructor
    private static class ForwardBinaryData extends SimpleChannelInboundHandler<ByteBuf>{

        private final ProxyMessageHandler handler;


        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg){
            log.info("收到客户端发送过来的请求数据");
            handler.handleTargetBinaryMessage(new DirectConnectChannel(ctx.channel()), ByteBufUtil.getBytes(msg));
        }
    }
}