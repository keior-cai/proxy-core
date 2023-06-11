package com.socks.proxy.protocol;

public interface TcpService{
    /**
     * 启动TCP服务
     */
    void start();


    /**
     * 关闭tcp服务
     */
    void close();


    /**
     * 停止TCP服务，没有关闭
     */
    void stop();
}
