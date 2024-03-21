package com.socks.proxy.protocol;

import com.socks.proxy.protocol.lifecycle.Lifecycle;

public interface TcpService extends Lifecycle{

    /**
     * 重启服务
     */
    void restart();
}
