package com.socks.proxy.protocol.handshake;

import com.socks.proxt.codes.ProxyMessage;
import com.socks.proxy.protocol.RemoteProxyConnect;

public interface ServerHandshakeMessageHandler extends HandshakeHandler{

    /**
     * 处理ss-server服务发送消息
     */
    void handle(RemoteProxyConnect local, ProxyMessage message, RemoteProxyConnect remote);

}
