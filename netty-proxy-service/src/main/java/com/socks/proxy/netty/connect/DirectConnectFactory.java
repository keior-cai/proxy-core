package com.socks.proxy.netty.connect;

import com.socks.proxy.handshake.connect.DirectConnectChannel;
import com.socks.proxy.handshake.handler.ForwardBinaryData;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.connect.ConnectProxyConnect;
import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.enums.ConnectType;
import com.socks.proxy.protocol.factory.ProxyFactory;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.ByteBufFormat;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.net.URI;
import java.util.concurrent.ExecutionException;

/**
 * @author: chuangjie
 * @date: 2024/2/2
 **/
@Slf4j
public class DirectConnectFactory implements ProxyFactory{

    private final Bootstrap bootstrap = new Bootstrap();


    public DirectConnectFactory(){
        int processors = Runtime.getRuntime().availableProcessors();
        EventLoopGroup group = new NioEventLoopGroup(processors * 2);
        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG, ByteBufFormat.HEX_DUMP));
    }


    @Override
    public ConnectProxyConnect create(TargetServer targetServer, ProxyMessageHandler handler){
        ChannelFuture connect = bootstrap.connect(targetServer.host(), targetServer.port());
        connect.channel().pipeline().addLast(new ForwardBinaryData(handler));
        try {
            connect.sync().get();
            ProxyConnect channel = new DirectConnectChannel(connect.channel());
            return new ConnectProxyConnect(){

                @Override
                public String channelId(){
                    return channel.channelId();
                }


                @Override
                public void write(byte[] binary){
                    channel.write(binary);
                }


                @Override
                public void write(String message){
                    channel.write(message);
                }


                @Override
                public void close(){
                    channel.close();
                }


                @Override
                public SocketAddress remoteAddress(){
                    return channel.remoteAddress();
                }


                @Override
                public void connect(){
                    // ignore
                }


                @Override
                public ConnectType type(){
                    return ConnectType.DIRECT;
                }
            };

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public long ping(){
        return 0;
    }


    @Override
    public URI uri(){
        return null;
    }
}
