package com.socks.proxy.protocol;

public interface TcpService{
    /**
     * 启动TCP服务
     */
    void start();


    /**
     * 关闭TCP服务
     */
    void stop();
}
