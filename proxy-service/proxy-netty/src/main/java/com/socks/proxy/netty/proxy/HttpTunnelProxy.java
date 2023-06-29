package com.socks.proxy.netty.proxy;

import com.socks.proxy.netty.connect.DefaultHttpNettyConnect;
import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.factory.LocalConnectServerFactory;
import com.socks.proxy.protocol.listener.LocalConnectListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * resolve http proxy implement
 *
 * @author: chuangjie
 * @date: 2022/11/20
 **/
@Slf4j
@ChannelHandler.Sharable
public class HttpTunnelProxy extends AbstractProxy<HttpRequest>{

    public HttpTunnelProxy(LocalConnectServerFactory connectFactory, List<LocalConnectListener> listeners,
                           ExecutorService executor){
        super(connectFactory, listeners, executor);
    }


    @Override
    protected TargetServer resolveRemoteServer(HttpRequest msg){
        return new HttpTargetServer(msg);
    }


    @Override
    protected LocalConnect createProxyConnect(ChannelHandlerContext ctx, TargetServer dstServer,
                                              List<LocalConnectListener> listeners){
        return new DefaultHttpNettyConnect(ctx, dstServer, listeners);
    }

}
