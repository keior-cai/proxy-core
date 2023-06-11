package com.socks.proxy.local;

import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.service.DefaultLocalServiceBuilder;

/**
 * @author: chuangjie
 * @date: 2023/6/4
 **/
public class Test{
    public static void main(String[] args){
        TcpService local = new DefaultLocalServiceBuilder().setPort(1082).builder();
        local.start();

    }
}
