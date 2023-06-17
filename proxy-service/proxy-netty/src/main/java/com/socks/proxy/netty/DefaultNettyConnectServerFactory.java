package com.socks.proxy.netty;

import com.socks.proxy.protocol.ServerMiddleProxy;
import com.socks.proxy.protocol.TargetConnect;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.factory.TargetConnectFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: chuangjie
 * @date: 2023/6/6
 **/
@Slf4j
public class DefaultNettyConnectServerFactory implements TargetConnectFactory{

    private final Bootstrap bootstrap = new Bootstrap();

    private final EventLoopGroup group;


    public DefaultNettyConnectServerFactory(){
        int processors = Runtime.getRuntime().availableProcessors();
        group = new NioEventLoopGroup(processors * 2);
        init();
    }


    @Override
    public TargetConnect getProxyService(ServerMiddleProxy channel, TargetServer target){
        return new SocketDstChannel(bootstrap, channel, target);
    }


    private void init(){
        bootstrap.group(group).channel(NioSocketChannel.class).handler(new LoggingHandler(LogLevel.DEBUG));
    }


    private static class SocketDstChannel implements TargetConnect{

        private final Bootstrap bootstrap;

        private final ServerMiddleProxy localConnect;

        private final TargetServer targetServer;

        private ChannelFuture channelFuture;


        public SocketDstChannel(Bootstrap bootstrap, ServerMiddleProxy channel, TargetServer targetServer){
            this.bootstrap = bootstrap;
            this.localConnect = channel;
            this.targetServer = targetServer;

        }


        @Override
        public String channelId(){
            return channelFuture.channel().id().asShortText();
        }


        @Override
        public void write(byte[] content){
            channelFuture.channel().writeAndFlush(Unpooled.wrappedBuffer(content));
        }


        @Override
        public void connect() throws Exception{
            this.channelFuture = bootstrap.connect(targetServer.host(), targetServer.port());
            this.channelFuture.channel().pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>(){
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg){
                    localConnect.write(ByteBufUtil.getBytes(msg));
                }
            });
            channelFuture.sync().get();
        }


        @Override
        public void close(){
            channelFuture.channel().close();
        }

    }
}
