package com.socks.proxy.protocol.handshake;

import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.codes.ProxyMessage;

/**
 * <pre>
 * ss-local 于ss-server协议消息处理器定义
 * 这里的消息定义可以是握手协议的消息
 * 也可以是ss-local通知ss-server处理请求消息
 * 也可以是ss-server通知ss-local处理请求消息
 * </pre>
 */
public interface LocalHandshakeMessageHandler<I extends ProxyMessage>{

    /**
     * <pre>
     *      处理ss-server发送过来的消息
     * </pre>
     *
     * @param local 本地连接，指代ss-local于system proxy（或者代理连接）socket通道 这里的连接一定是使用的上层协议可能存在多种
     * @param message ss-server 发送过来的消息对象
     *         这里的消息对象是由ss-server发送过来经过{@link com.socks.proxy.protocol.codes.ProxyCommandDecode}解析之后得到的
     * @param remote 远程连接指代ss-local于ss-server之间的连接，也有可能不是连接例如UDP之类的
     */
    void handle(ProxyConnect local, I message, ProxyConnect remote);

}
