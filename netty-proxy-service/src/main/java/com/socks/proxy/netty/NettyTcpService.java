package com.socks.proxy.netty;

import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.protocol.exception.LifecycleException;
import com.socks.proxy.protocol.lifecycle.LifecycleBean;
import com.socks.proxy.protocol.lifecycle.LifecycleState;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.ByteBufFormat;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.BindException;

/**
 * netty tcp 服务
 *
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@Slf4j
public class NettyTcpService extends LifecycleBean implements TcpService{

    private final int port;

    private final String bindHost;

    private ServerBootstrap bootstrap;

    private EventLoopGroup childGroup;

    private EventLoopGroup masterGroup;

    private final ChannelHandler handler;

    private ChannelFuture future;


    public NettyTcpService(int port, ChannelHandler handler){
        this(port, "0.0.0.0", handler);
    }


    public NettyTcpService(int port, String bindHost, ChannelHandler handler){
        this.port = port;
        this.bindHost = bindHost;
        this.handler = handler;
    }


    @Override
    protected void initInternal(){
        int cpuNum = Runtime.getRuntime().availableProcessors();
        childGroup = new NioEventLoopGroup(cpuNum * 2, new NamedThreadFactory("reactive-child-", false));
        masterGroup = new NioEventLoopGroup(cpuNum, new NamedThreadFactory("reactive-master-", false));
        this.bootstrap = new ServerBootstrap();
        bootstrap.group(masterGroup, childGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 2048).option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_TIMEOUT, 1000)
                .handler(new LoggingHandler(LogLevel.DEBUG, ByteBufFormat.HEX_DUMP)).childHandler(handler);
    }


    @Override
    protected void startInternal(){
        try {
            future = bootstrap.bind(bindHost, port);
            setStateInternal(LifecycleState.STARTING, future);
            future.sync();
            log.info("start service port is = {}", port);
        } catch (InterruptedException e) {
            throw new Error(e);
        } catch (Exception e) {
            if(e instanceof BindException){
                log.error("port = {} already use please use other port.", port);
            }
            throw e;
        }
    }


    @Override
    protected void stopInternal(){
        fireLifecycleEvent(STOP_EVENT, future);
        try {
            future.channel().close().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void destroyInternal(){
        childGroup.shutdownGracefully();
        masterGroup.shutdownGracefully();
    }


    @Override
    public void restart(){
        try {
            stop();
            start();
        } catch (LifecycleException e) {
            throw new RuntimeException("Restart fail", e);
        }
    }

}
