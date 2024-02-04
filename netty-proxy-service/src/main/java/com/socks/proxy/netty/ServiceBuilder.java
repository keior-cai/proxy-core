package com.socks.proxy.netty;

import com.socks.proxy.protocol.TcpService;

public interface ServiceBuilder{

    TcpService builder();
}
