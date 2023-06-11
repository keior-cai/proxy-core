package com.socks.proxy.netty;

import com.socks.proxy.protocol.TcpService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
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
public class AbstractNettyTcpService implements TcpService{

    private final int port;

    private final String bindHost;

    private final ServerBootstrap bootstrap = new ServerBootstrap();

    private final EventLoopGroup childGroup;

    private final EventLoopGroup masterGroup;

    private final ChannelHandler handler;

    private Thread thread;


    public AbstractNettyTcpService(int port, ChannelHandler handler){
        this(port, "0.0.0.0", handler);
    }


    public AbstractNettyTcpService(int port, String bindHost, ChannelHandler handler){
        this.port = port;
        this.bindHost = bindHost;
        int cpuNum = Runtime.getRuntime().availableProcessors();
        childGroup = new NioEventLoopGroup(cpuNum * 2, new NamedThreadFactory("reactive-child-", false));
        masterGroup = new NioEventLoopGroup(cpuNum, new NamedThreadFactory("reactive-master-", false));
        this.handler = handler;
        initializer();
    }


    @Override
    public void start(){
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
            }
        }, "tcp-server-main");
        thread.setDaemon(false);
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();
    }


    @Override
    public void stop(){
        if(thread == null || thread.isInterrupted()){
            return;
        }
        thread.interrupt();
    }


    public void close(){
        stop();
        masterGroup.shutdownGracefully();
        childGroup.shutdownGracefully();
    }


    public void initializer(){
        bootstrap.group(masterGroup, childGroup).channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG, ByteBufFormat.HEX_DUMP))
                .childHandler(new ChannelInitializer<Channel>(){
                    @Override
                    protected void initChannel(Channel ch){
                        ch.pipeline().addLast(new LoggingHandler()).addLast(handler);
                    }
                });
    }
}
