package com.socks.proxy.netty;

import com.socks.proxy.protocol.TcpService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.ByteBufFormat;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@Slf4j
public abstract class AbstractNettyTcpService implements TcpService{

    private final int port;

    private final String bindHost;

    private ServerBootstrap bootstrap;

    private EventLoopGroup childGroup;

    private EventLoopGroup masterGroup;

    private final ChannelHandler handler;

    private Thread thread;


    public AbstractNettyTcpService(int port, ChannelHandler handler){
        this(port, "0.0.0.0", handler);
    }


    public AbstractNettyTcpService(int port, String bindHost, ChannelHandler handler){
        this.port = port;
        this.bindHost = bindHost;
        this.handler = handler;
    }


    @Override
    public void start(){
        initializer();
        ChannelFuture future = bootstrap.bind(bindHost, port);
        try {
            future.sync();
        } catch (InterruptedException e) {
            close();
            return;
        }
        thread = new Thread(()->{
            try {
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                // ignore
                future.channel().close();
            }
        }, "tcp-server-main");
        thread.setDaemon(false);
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();
    }


    private void stop(){
        if(thread == null || thread.isInterrupted()){
            return;
        }
        thread.interrupt();
    }


    public void close(){
        stop();
        masterGroup.shutdownGracefully();
        childGroup.shutdownGracefully();
        this.bootstrap = null;
        this.masterGroup = null;
        this.childGroup = null;
    }


    public void initializer(){
        int cpuNum = Runtime.getRuntime().availableProcessors();
        childGroup = new NioEventLoopGroup(cpuNum * 2, new NamedThreadFactory("reactive-child-", false));
        masterGroup = new NioEventLoopGroup(cpuNum, new NamedThreadFactory("reactive-master-", false));
        this.bootstrap = new ServerBootstrap();
        bootstrap.group(masterGroup, childGroup).channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG, ByteBufFormat.HEX_DUMP)).childHandler(handler);
    }
}
