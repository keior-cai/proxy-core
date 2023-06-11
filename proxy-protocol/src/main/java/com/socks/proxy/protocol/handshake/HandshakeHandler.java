package com.socks.proxy.protocol.handshake;

import com.socks.proxy.protocol.command.ProxyCommand;

public interface HandshakeHandler{

    ProxyCommand command();
}
