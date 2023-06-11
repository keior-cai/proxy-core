package com.socks.proxy.protocol.handshake;

import com.socks.proxt.codes.ProxyMessage;
import com.socks.proxy.protocol.LocalProxyConnect;
import com.socks.proxy.protocol.RemoteProxyConnect;

public interface LocalHandshakeMessageHandler extends HandshakeHandler{

    void handle(LocalProxyConnect local, ProxyMessage message, RemoteProxyConnect remote);

}
