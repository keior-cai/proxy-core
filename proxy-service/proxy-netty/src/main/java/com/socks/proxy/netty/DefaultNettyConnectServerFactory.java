package com.socks.proxy.netty;

import com.socks.proxy.netty.constant.AttrConstant;
import com.socks.proxy.protocol.DstServer;
import com.socks.proxy.protocol.ICipher;
import com.socks.proxy.protocol.RemoteProxyConnect;
import com.socks.proxy.protocol.factory.ServerConnectTargetFactory;
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
public class DefaultNettyConnectServerFactory implements ServerConnectTargetFactory{

    private final Bootstrap bootstrap = new Bootstrap();

    private final EventLoopGroup group;


    public DefaultNettyConnectServerFactory(){
        int processors = Runtime.getRuntime().availableProcessors();
        group = new NioEventLoopGroup(processors * 2);
        init();
    }


    @Override
    public RemoteProxyConnect getProxyService(RemoteProxyConnect channel){
        SocketDstChannel socketDstChannel = new SocketDstChannel(bootstrap, channel);
        socketDstChannel.setDstServer(channel.getDstServer());
        return socketDstChannel;
    }


    private void init(){
        bootstrap.group(group).channel(NioSocketChannel.class).handler(new LoggingHandler(LogLevel.DEBUG));
    }


    private static class SocketDstChannel implements RemoteProxyConnect{

        private final Bootstrap bootstrap;

        private final RemoteProxyConnect localProxyConnect;

        private DstServer dstServer;

        private ChannelFuture channel;


        public SocketDstChannel(Bootstrap bootstrap, RemoteProxyConnect localProxyConnect){
            this.bootstrap = bootstrap;
            this.localProxyConnect = localProxyConnect;
        }


        @Override
        public String channelId(){
            return channel.channel().id().asShortText();
        }


        @Override
        public void write(byte[] content){
            channel.channel().writeAndFlush(Unpooled.wrappedBuffer(content));
        }


        @Override
        public void connect() throws Exception{
            this.channel = bootstrap.connect(dstServer.host(), dstServer.port());
            this.channel.channel().pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>(){
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg){
                    localProxyConnect.write(ByteBufUtil.getBytes(msg));
                }
            });
            channel.sync().get();
        }


        @Override
        public void close(){
            channel.channel().close();
        }


        @Override
        public void setCipher(ICipher iCipher){
            channel.channel().attr(AttrConstant.CIPHER_KEY).set(iCipher);
        }


        @Override
        public void write(String message){
            throw new IllegalStateException();

        }


        @Override
        public DstServer getDstServer(){
            return dstServer;
        }


        @Override
        public void setDstServer(DstServer dstServer){
            this.dstServer = dstServer;
        }


        @Override
        public void setTarget(RemoteProxyConnect proxyConnect){
            // ignore
        }

    }
}
