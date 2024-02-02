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
     * 重启服务
     */
    void restart();
}
