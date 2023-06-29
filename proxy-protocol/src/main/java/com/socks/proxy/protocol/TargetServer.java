package com.socks.proxy.protocol;

import com.socks.proxy.protocol.enums.Protocol;

/**
 * <p>define target server InetAddress</p>
 * {@link com.socks.proxy.protocol.DefaultTargetServer}
 */
public interface TargetServer{

    /**
     * notify target server domain or ip of proxy server service
     *
     * @return if domain is local request connect of target server, TargetServer will return domain name if ip is local
     *         request connect of target server, TargetServer will return ip
     */
    String host();


    int port();


    /**
     *
     */
    Protocol sourceProtocol();
}
