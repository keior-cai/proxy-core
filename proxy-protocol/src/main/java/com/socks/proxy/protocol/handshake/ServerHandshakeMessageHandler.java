package com.socks.proxy.protocol.handshake;

import com.socks.proxy.protocol.RemoteProxyConnect;
import com.socks.proxy.protocol.codes.ProxyMessage;

public interface ServerHandshakeMessageHandler{

    /**
     * 处理ss-server服务发送消息
     */
    void handle(RemoteProxyConnect local, ProxyMessage message, RemoteProxyConnect remote);

}
