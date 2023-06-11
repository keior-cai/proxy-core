package com.socks.proxy.protocol;

public interface LocalProxyConnect extends ProxyConnect{

    /**
     * 发送连接成功
     */
    void writeConnectSuccess();


    /**
     * 发送连接失败
     */
    void writeConnectFail();


    /**
     * 设置远程链接通道
     */
    void setRemoteChannel(RemoteProxyConnect channel);


    /**
     * 通道设置属性
     */
    void setCipher(ICipher iCipher);


    DstServer getDstServer();

}
