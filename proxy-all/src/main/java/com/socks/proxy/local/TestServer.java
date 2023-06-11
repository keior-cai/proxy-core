package com.socks.proxy.local;

import com.socks.proxy.protocol.TcpService;
import com.socks.proxy.service.DefaultServerServiceBuilder;

/**
 * @author: chuangjie
 * @date: 2023/6/10
 **/
public class TestServer{
    public static void main(String[] args){
        TcpService service = new DefaultServerServiceBuilder()
                .setPort(8082).builder();
        service.start();
    }
}
