package com.socks.proxy.protocol.factory;

import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.LocalMiddleProxy;

/**
 * 定义连接远程服务工厂
 */
public interface LocalConnectServerFactory{

    /**
     * 获取一个连接远程服务实例
     *
     * @param channel 客户端连接实例
     * @return 连接远程服务实例
     */
    LocalMiddleProxy getProxyService(LocalConnect channel);
}
