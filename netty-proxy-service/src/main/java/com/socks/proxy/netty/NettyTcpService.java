package com.socks.proxy.netty;

import com.socks.proxy.protocol.TcpService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.BindException;

/**
 * netty tcp 服务
 *
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@Slf4j
public class NettyTcpService implements TcpService{

    private final int port;

    private final String bindHost;

    private ServerBootstrap bootstrap;

    private EventLoopGroup childGroup;

    private EventLoopGroup masterGroup;

    private final ChannelHandler handler;

    private Thread thread;


    public NettyTcpService(int port, ChannelHandler handler){
        this(port, "0.0.0.0", handler);
    }


    public NettyTcpService(int port, String bindHost, ChannelHandler handler){
        this.port = port;
        this.bindHost = bindHost;
        this.handler = handler;
    }


    @Override
    public void start(){
        try {
            initializer();
            ChannelFuture future = bootstrap.bind(bindHost, port);
            future.sync();
            thread = new Thread(()->{
                try {
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    // ignore
                    future.channel().close();
                } finally {
                    stop();
                }
            }, "tcp-server-main");
            thread.setDaemon(false);
            thread.setPriority(Thread.NORM_PRIORITY);
            thread.start();
            log.info("start service port is = {}", port);
        } catch (InterruptedException e) {
            close();
        } catch (Exception e) {
            if(e instanceof BindException){
                log.error("port = {} already use please use other port.", port);
            }
            close();
            throw e;
        }
    }


    private void stop(){
        if(masterGroup != null){
            masterGroup.shutdownGracefully();
        }
        if(childGroup != null){
            childGroup.shutdownGracefully();
        }
        this.bootstrap = null;
        this.masterGroup = null;
        this.childGroup = null;
    }


    @Override
    public void close(){
        if(thread == null || thread.isInterrupted()){
            return;
        }
        thread.interrupt();
        log.info("tcp service close success");
    }


    @Override
    public void restart(){
        close();
        start();
    }


    public void initializer(){
        int cpuNum = Runtime.getRuntime().availableProcessors();
        childGroup = new NioEventLoopGroup(cpuNum * 2, new NamedThreadFactory("reactive-child-", false));
        masterGroup = new NioEventLoopGroup(cpuNum, new NamedThreadFactory("reactive-master-", false));
        this.bootstrap = new ServerBootstrap();
        bootstrap.group(masterGroup, childGroup).channel(NioServerSocketChannel.class).handler(initHandler())
                .childHandler(handler);
    }


    protected ChannelHandler initHandler(){
        return new ChannelHandlerAdapter(){

        };
    }

}
