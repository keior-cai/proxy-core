package com.socks.proxy.protocol.factory;

import com.socks.proxy.protocol.ServerMiddleProxy;
import com.socks.proxy.protocol.TargetConnect;
import com.socks.proxy.protocol.TargetServer;

/**
 * 定义连接远程服务工厂
 */
public interface TargetConnectFactory{

    /**
     * 获取一个连接远程服务实例
     *
     * @param channel 客户端连接实例
     * @return 连接远程服务实例
     */
    TargetConnect getProxyService(ServerMiddleProxy channel, TargetServer target);
}
