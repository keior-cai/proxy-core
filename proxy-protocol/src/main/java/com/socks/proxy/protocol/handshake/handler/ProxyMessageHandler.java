package com.socks.proxy.protocol.handshake.handler;

import com.socks.proxy.protocol.connect.ProxyConnect;

import java.util.Map;

public interface ProxyMessageHandler{


    /**
     * <pre>
     * 处理ss-server服务处理websocket握手成功之后的连接信息
     * 在这里可以处理业务例如鉴权
     * 通过之后，ss-server会发送public key给到客户端
     * </pre>
     */
    void handlerShakeEvent(ProxyConnect local, Map<String, Object> context);


    /**
     * 处理客户端发送文本请求数据
     *
     * @param local 客户端连接
     * @param text 文本请求数据
     */
    void handleLocalTextMessage(ProxyConnect local, String text);


    /**
     * 处理客户端请求数据
     *
     * @param local 客户端连接
     * @param binary 客户端请求数据
     */
    void handleLocalBinaryMessage(ProxyConnect local, byte[] binary);


    /**
     * 处理目标服务发送的响应数据
     *
     * @param target 目标服务
     * @param binary 目标服务响应数据
     */
    void handleTargetBinaryMessage(ProxyConnect target, byte[] binary);


    /**
     * 目标服务断开连接
     */
    void handleTargetClose(ProxyConnect target, String reason);


    /**
     * 处理客户端断开连接
     */
    void handleLocalClose(ProxyConnect local, String reason);
}
