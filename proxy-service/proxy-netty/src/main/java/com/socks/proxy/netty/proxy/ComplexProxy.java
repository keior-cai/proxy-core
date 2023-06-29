package com.socks.proxy.netty.proxy;

import com.socks.proxy.netty.connect.DefaultHttpNettyConnect;
import com.socks.proxy.netty.connect.DefaultSocks5NettyConnect;
import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.factory.LocalConnectServerFactory;
import com.socks.proxy.protocol.listener.LocalConnectListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author: chuangjie
 * @date: 2023/6/29
 **/
public class ComplexProxy extends AbstractProxy<Object>{

    public ComplexProxy(LocalConnectServerFactory factory, List<LocalConnectListener> listeners,
                        ExecutorService executor){
        super(factory, listeners, executor);
    }


    @Override
    protected TargetServer resolveRemoteServer(Object msg){
        if(msg instanceof HttpRequest){
            return new HttpTargetServer((HttpRequest) msg);
        } else if(msg instanceof Socks5CommandRequest){
            return new Socks5TargetServer((Socks5CommandRequest) msg);
        }
        throw new IllegalStateException();
    }


    @Override
    protected LocalConnect createProxyConnect(ChannelHandlerContext ctx, TargetServer dstServer,
                                              List<LocalConnectListener> listeners){
        switch(dstServer.sourceProtocol()) {
            case HTTP:
                return new DefaultHttpNettyConnect(ctx, dstServer, listeners);
            case SOCKS5:
            default:
                return new DefaultSocks5NettyConnect(ctx, dstServer, listeners);
        }
    }
}
