package com.socks.proxy.protocol.factory;

import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.connect.RegisterProxyConnect;
import com.socks.proxy.protocol.handshake.handler.ProxyMessageHandler;

import java.io.IOException;

public interface ProxyFactory{

    RegisterProxyConnect create(TargetServer targetServer, ProxyMessageHandler handler) throws IOException;
}
