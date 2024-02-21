package com.socks.proxy.netty.proxy;

import com.socks.proxy.protocol.DefaultTargetServer;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.enums.Protocol;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author: chuangjie
 * @date: 2023/6/29
 **/
public class HttpTargetServer implements TargetServer{

    private final TargetServer proxy;


    public HttpTargetServer(FullHttpRequest request){
        proxy = resolveTargetAddress(request);
    }


    private TargetServer resolveTargetAddress(FullHttpRequest httpMsg){
        String uri = httpMsg.uri();
        if(uri.startsWith("http://") || uri.startsWith("https://")){
            try {
                URL url = new URL(uri);
                return new DefaultTargetServer(url.getHost(), url.getPort() == -1 ? 80 : url.getPort(), Protocol.HTTP);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(httpMsg.uri() + " is getDstAddress fail");
            }
        } else {
            String host = uri.contains(":") ? uri.substring(0, uri.lastIndexOf(":")) : uri;
            int port = uri.contains(":") ? Integer.parseInt(uri.substring(uri.lastIndexOf(":") + 1)) : 80;
            return new DefaultTargetServer(host, port, Protocol.HTTPS);
        }
    }


    @Override
    public String host(){
        return proxy.host();
    }


    @Override
    public int port(){
        return proxy.port();
    }


    @Override
    public Protocol sourceProtocol(){
        return proxy.sourceProtocol();
    }

    public String toString(){
        return sourceProtocol()+"://"+host()+":"+port();
    }

}
