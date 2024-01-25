package com.socks.proxy.protocol.handshake.handler;

import com.socks.proxy.protocol.connect.ProxyConnect;

import java.util.Map;

/**
 * server 握手服务
 */
public interface ServerHandshakeListener{

    /**
     * ss-server 处理客户端握手信息
     *
     * @param local 客户端连接
     * @param message 客户端发送过来的文本消息
     */
    void localTextMessage(ProxyConnect local, String message);


    /**
     * 处理客户端发送过来的需要代理给到目标服务的数据
     *
     * @param target 目标服务连接
     * @param local 客户端连接
     * @param binary 客户端发送需要给到目标服务的数据
     */
    void localBinaryMessage(ProxyConnect target, ProxyConnect local, byte[] binary);


    /**
     * 处理连接目标服务连接
     *
     * @param target 目标代理链接
     * @param local 本地代理连接
     */
    void connectTarget(ProxyConnect target, ProxyConnect local);


    /**
     * ss-server 处理客户端连接关闭消息
     *
     * @param local 客户端代理连接
     * @param reason 关闭原因
     * @param isServer 是否是服务端关闭
     */
    void closeMessage(ProxyConnect local, String reason, boolean isServer);

}
