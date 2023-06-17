package com.socks.proxy.protocol;

import lombok.AllArgsConstructor;

/**
 * <p>target server InetAddress of default implement </p>
 * {@link com.socks.proxy.protocol.TargetServer}
 *
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@AllArgsConstructor
public class DefaultTargetServer implements TargetServer{

    private String host;

    private int port;


    @Override
    public String host(){
        return host;
    }


    @Override
    public int port(){
        return port;
    }
}
