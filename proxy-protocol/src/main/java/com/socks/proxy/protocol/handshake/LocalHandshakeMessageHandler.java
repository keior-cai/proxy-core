package com.socks.proxy.protocol.handshake;

import com.socks.proxy.protocol.LocalProxyConnect;
import com.socks.proxy.protocol.RemoteProxyConnect;
import com.socks.proxy.protocol.codes.ProxyMessage;

public interface LocalHandshakeMessageHandler{

    void handle(LocalProxyConnect local, ProxyMessage message, RemoteProxyConnect remote);

}
