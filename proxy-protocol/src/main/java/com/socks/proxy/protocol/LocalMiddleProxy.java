package com.socks.proxy.protocol;

import com.socks.proxy.protocol.enums.ConnectStatus;

/**
 * 定义远程服务ss-server连接对象
 */
public interface LocalMiddleProxy extends ProxyConnect{

    void write(String message);


    /**
     * 连接远程服务
     */
    void connect() throws Exception;


    ConnectStatus status();


    void setTarget(LocalMiddleProxy proxyConnect);

}
