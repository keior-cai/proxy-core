package com.socks.proxy.protocol.handshake;

import com.socks.proxy.protocol.codes.ProxyMessage;
import com.socks.proxy.protocol.connect.ProxyConnect;

public interface ServerHandshakeMessageHandler{

    /**
     * 处理ss-server服务发送消息
     */
    void handle(ProxyConnect local, ProxyMessage message, ProxyConnect target);

}
