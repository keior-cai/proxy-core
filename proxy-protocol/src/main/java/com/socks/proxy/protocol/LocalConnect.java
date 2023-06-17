package com.socks.proxy.protocol;

/**
 * <pre>
 *      define connect server service of local request According to rfc
 *      system supper socks5,socks4,http(s) proxy
 * </pre>
 */
public interface LocalConnect extends ProxyConnect{

    /**
     * send connect successfully to system or proxy client by server service notify
     */
    void writeConnectSuccess();


    /**
     * send connect failed to system or proxy client by server service notify
     */
    void writeConnectFail();


    /**
     * <pre>
     *    setting server service channel
     *    system sending data to this channel that will forward data to target server by this channel
     * </pre>
     */
    void setRemoteChannel(LocalMiddleProxy channel);


    /**
     * <pre>
     *     After handshake protocol authentication successful
     *     local service use {@link com.socks.proxy.protocol.TargetServer} send target InetAddress to server service
     * </pre>
     *
     * @return {@link com.socks.proxy.protocol.DefaultTargetServer}
     */
    TargetServer getDstServer();


    /**
     * 通道设置属性
     */
    void setCipher(ICipher iCipher);

}
