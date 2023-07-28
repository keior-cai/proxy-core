package com.socks.proxy.protocol.factory;

import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.LocalMiddleService;
import com.socks.proxy.protocol.TargetServer;

/**
 * 定义连接远程服务工厂
 */
public interface LocalConnectServerFactory{

    /**
     * 获取一个连接远程服务实例
     *
     * @param channel 客户端连接实例
     * @param remoteServer 目标服务地址
     * @return 连接远程服务实例
     */
    LocalMiddleService getProxyService(LocalConnect channel, TargetServer remoteServer);
}
