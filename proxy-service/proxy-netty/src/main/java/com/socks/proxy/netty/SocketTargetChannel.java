package com.socks.proxy.netty;

import com.socks.proxy.protocol.ServerMiddleProxy;
import com.socks.proxy.protocol.TargetConnect;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.listener.TargetConnectListener;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

public class SocketTargetChannel implements TargetConnect{

    private final Bootstrap bootstrap;

    private final ServerMiddleProxy localConnect;

    private final TargetServer targetServer;

    private final List<TargetConnectListener> listeners;

    private ChannelFuture channelFuture;


    public SocketTargetChannel(Bootstrap bootstrap, ServerMiddleProxy channel, TargetServer targetServer,
                               List<TargetConnectListener> listeners){
        this.bootstrap = bootstrap;
        this.localConnect = channel;
        this.targetServer = targetServer;
        this.listeners = listeners;

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
        this.channelFuture.channel().pipeline().addLast(new TargetForWard(localConnect, listeners));
        channelFuture.sync().get();
        for(TargetConnectListener listener : listeners) {
            listener.onConnected(this, targetServer);
        }
    }


    @Override
    public void close(){
        channelFuture.channel().close();
    }


    private static class TargetForWard extends SimpleChannelInboundHandler<ByteBuf>{

        private final ServerMiddleProxy delegate;

        private final List<TargetConnectListener> listeners;


        public TargetForWard(ServerMiddleProxy proxy, List<TargetConnectListener> listeners){
            this.delegate = proxy;
            this.listeners = listeners;
        }


        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg){
            try {
                byte[] bytes = ByteBufUtil.getBytes(msg);
                for(TargetConnectListener listener : listeners) {
                    listener.onBinary(delegate, bytes);
                }
                delegate.write(bytes);
            } catch (Throwable cause) {
                for(TargetConnectListener listener : listeners) {
                    listener.onCallbackError(delegate, cause);
                }
            }

        }


        @Override
        public void channelInactive(ChannelHandlerContext ctx){
            for(TargetConnectListener listener : listeners) {
                listener.onDisconnected(delegate);
            }
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
            for(TargetConnectListener listener : listeners) {
                listener.onError(delegate, cause);
            }
        }
    }

}
