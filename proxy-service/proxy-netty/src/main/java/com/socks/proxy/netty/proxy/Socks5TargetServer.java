package com.socks.proxy.netty.proxy;

import com.socks.proxy.protocol.DefaultTargetServer;
import com.socks.proxy.protocol.enums.Protocol;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest;

/**
 * @author: chuangjie
 * @date: 2023/6/29
 **/
public class Socks5TargetServer extends DefaultTargetServer{
    public Socks5TargetServer(Socks5CommandRequest request){
        super(request.dstAddr(), request.dstPort(), Protocol.SOCKS5);
    }

}
