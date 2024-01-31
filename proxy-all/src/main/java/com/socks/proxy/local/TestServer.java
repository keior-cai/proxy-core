package com.socks.proxy.local;

import com.socks.proxy.netty.ServerServiceBuilder;
import com.socks.proxy.protocol.TcpService;

/**
 * @author: chuangjie
 * @date: 2023/6/10
 **/
public class TestServer{
    public static void main(String[] args){
        TcpService service = new ServerServiceBuilder().setPort(8083).builder();
        service.start();
        System.out.println("启动成功");
    }
}
