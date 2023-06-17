package com.socks.proxy.netty;

import com.socks.proxy.protocol.ServerMiddleProxy;
import com.socks.proxy.protocol.TargetConnect;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.factory.TargetConnectFactory;
import com.socks.proxy.protocol.listener.TargetConnectListener;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author: chuangjie
 * @date: 2023/6/6
 **/
@Slf4j
public class DefaultNettyConnectServerFactory implements TargetConnectFactory{

    private final Bootstrap bootstrap = new Bootstrap();

    private final EventLoopGroup group;

    private final List<TargetConnectListener> listeners;


    public DefaultNettyConnectServerFactory(List<TargetConnectListener> listeners){
        this.listeners = listeners;
        int processors = Runtime.getRuntime().availableProcessors();
        group = new NioEventLoopGroup(processors * 2);
        init();
    }


    @Override
    public TargetConnect getProxyService(ServerMiddleProxy channel, TargetServer target){
        return new SocketTargetChannel(bootstrap, channel, target, listeners);
    }


    private void init(){
        bootstrap.group(group).channel(NioSocketChannel.class).handler(new LoggingHandler(LogLevel.DEBUG));
    }

}
