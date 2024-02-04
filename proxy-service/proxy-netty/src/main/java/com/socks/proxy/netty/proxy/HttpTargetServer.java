package com.socks.proxy.netty.proxy;

import com.socks.proxy.protocol.DefaultTargetServer;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.enums.Protocol;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author: chuangjie
 * @date: 2023/6/29
 **/
public class HttpTargetServer implements TargetServer{

    private final TargetServer proxy;


    public HttpTargetServer(HttpRequest request){
        proxy = resolveTargetAddress(request);
    }


    private TargetServer resolveTargetAddress(HttpRequest httpMsg){
        String uri = httpMsg.uri();
        String host = uri.contains(":") ? uri.substring(0, uri.lastIndexOf(":")) : uri;
        int port = uri.contains(":") ? Integer.parseInt(uri.substring(uri.lastIndexOf(":") + 1)) : 80;
        return new DefaultTargetServer(host, port, Protocol.HTTP);
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
}
