package com.socks.proxy.protocol;

/**
 * 定义远程服务ss-server连接对象
 */
public interface RemoteProxyConnect extends ProxyConnect{

    void write(String message);


    /**
     * 连接远程服务
     */
    void connect() throws Exception;


    DstServer getDstServer();


    void setDstServer(DstServer dstServer);


    void setTarget(RemoteProxyConnect proxyConnect);

}
