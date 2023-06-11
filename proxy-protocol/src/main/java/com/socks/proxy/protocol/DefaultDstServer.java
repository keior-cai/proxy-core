package com.socks.proxy.protocol;

import lombok.AllArgsConstructor;

/**
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@AllArgsConstructor
public class DefaultDstServer implements DstServer{

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
