package com.socks.proxy.netty.proxy;

import com.socks.proxy.netty.connect.DefaultHttpNettyConnect;
import com.socks.proxy.protocol.*;
import com.socks.proxy.protocol.factory.LocalConnectServerFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * http 代理处理器
 *
 * @author: chuangjie
 * @date: 2022/11/20
 **/
@Slf4j
@ChannelHandler.Sharable
public class HttpProxy extends AbstractProxy<HttpRequest>{

    public HttpProxy(LocalConnectServerFactory connectFactory){
        super(connectFactory);
    }


    @Override
    protected DstServer resolveRemoteServer(HttpRequest msg){
        return getDstAddress(msg);
    }


    @Override
    protected LocalProxyConnect createProxyConnect(ChannelHandlerContext ctx, DstServer dstServer){
        return new DefaultHttpNettyConnect(ctx);
    }


    private DstServer getDstAddress(HttpRequest httpMsg){
        String uri = httpMsg.uri();
        if(uri.startsWith("http://") || uri.startsWith("https://")){
            try {
                URL url = new URL(uri);
                return new DefaultDstServer(url.getHost(), url.getPort() == -1 ? 80 : url.getPort());
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(httpMsg.uri() + " is getDstAddress fail");
            }
        } else {
            String host = uri.contains(":") ? uri.substring(0, uri.lastIndexOf(":")) : uri;
            int port = uri.contains(":") ? Integer.parseInt(uri.substring(uri.lastIndexOf(":") + 1)) : 80;
            return new DefaultDstServer(host, port);
        }
    }

}
