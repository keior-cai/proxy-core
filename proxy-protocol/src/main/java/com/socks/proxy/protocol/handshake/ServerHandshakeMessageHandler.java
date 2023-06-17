package com.socks.proxy.protocol.handshake;

import com.socks.proxy.protocol.LocalMiddleProxy;
import com.socks.proxy.protocol.ServerMiddleProxy;
import com.socks.proxy.protocol.TargetConnect;
import com.socks.proxy.protocol.codes.ProxyMessage;

public interface ServerHandshakeMessageHandler{

    /**
     * 处理ss-server服务发送消息
     */
    void handle(ServerMiddleProxy local, ProxyMessage message, TargetConnect remote);

}
