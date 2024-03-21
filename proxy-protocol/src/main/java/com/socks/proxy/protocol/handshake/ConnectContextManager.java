package com.socks.proxy.protocol.handshake;

import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.handshake.handler.ProxyContext;

import java.util.Set;

/**
 * 连接上下文管理器
 */
public interface ConnectContextManager{

    /**
     * 向管理器添加一个连接上下文
     *
     */
    ProxyContext putLocalConnect(ProxyConnect connect);


    /**
     * 创建一个远程连接
     */
    void putTargetConnect(ProxyConnect connect, ProxyConnect dst);


    /**
     * 从连接管理器移除全部关联上下文
     */
    void removeAll(ProxyConnect connect);


    /**
     * 获取连接上下文
     */
    ProxyContext getContext(ProxyConnect connect);


    /**
     * 创建一个代理连接
     */
    void putProxyConnect(ProxyConnect connect);


    /**
     * 查询连接管理器全部连接
     */
    Set<ProxyContext> getTargetAllProxy();
}
