package com.socks.proxy.netty.proxy;

import com.socks.proxy.netty.connect.DefaultHttpNettyConnect;
import com.socks.proxy.protocol.DefaultTargetServer;
import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.factory.LocalConnectServerFactory;
import com.socks.proxy.protocol.listener.LocalConnectListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * resolve http proxy implement
 *
 * @author: chuangjie
 * @date: 2022/11/20
 **/
@Slf4j
@ChannelHandler.Sharable
public class HttpTunnelProxy extends AbstractProxy<HttpRequest>{

    public HttpTunnelProxy(LocalConnectServerFactory connectFactory, List<LocalConnectListener> listeners){
        super(connectFactory, listeners);
    }


    @Override
    protected TargetServer resolveRemoteServer(HttpRequest msg){
        return resolveTargetAddress(msg);
    }


    @Override
    protected LocalConnect createProxyConnect(ChannelHandlerContext ctx, TargetServer dstServer){
        return new DefaultHttpNettyConnect(ctx, dstServer);
    }


    /**
     * resolve target InetAddress by http connect request
     *
     * @param httpMsg
     * @return target InetAddress {@link  }
     */
    private TargetServer resolveTargetAddress(HttpRequest httpMsg){
        String uri = httpMsg.uri();
        if(uri.startsWith("http://") || uri.startsWith("https://")){
            try {
                URL url = new URL(uri);
                return new DefaultTargetServer(url.getHost(), url.getPort() == -1 ? 80 : url.getPort());
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(httpMsg.uri() + " is getDstAddress fail");
            }
        } else {
            String host = uri.contains(":") ? uri.substring(0, uri.lastIndexOf(":")) : uri;
            int port = uri.contains(":") ? Integer.parseInt(uri.substring(uri.lastIndexOf(":") + 1)) : 80;
            return new DefaultTargetServer(host, port);
        }
    }

}
