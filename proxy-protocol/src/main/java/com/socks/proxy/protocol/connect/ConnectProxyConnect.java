package com.socks.proxy.protocol.connect;

/**
 * 注册事件监听代理连接
 *
 * @author: chuangjie
 * @date: 2024/1/24
 **/
public interface ConnectProxyConnect extends ProxyConnect{

    /**
     * 创建连接
     */
    void connect();

}
