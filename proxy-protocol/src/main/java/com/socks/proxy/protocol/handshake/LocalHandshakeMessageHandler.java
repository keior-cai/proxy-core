package com.socks.proxy.protocol.handshake;

import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.LocalMiddleProxy;
import com.socks.proxy.protocol.codes.ProxyMessage;

public interface LocalHandshakeMessageHandler{

    void handle(LocalConnect local, ProxyMessage message, LocalMiddleProxy remote);

}
