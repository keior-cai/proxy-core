package com.socks.proxy.protocol.handshake;

import com.socks.proxy.protocol.LocalProxyConnect;
import com.socks.proxy.protocol.RemoteProxyConnect;
import com.socks.proxy.protocol.codes.ProxyMessage;

/**
 * @author: chuangjie
 * @date: 2023/6/11
 **/
@SuppressWarnings("unchecked")
public abstract class SimpleLocalHandshakeMessageHandler<I extends ProxyMessage>
        implements LocalHandshakeMessageHandler{

    @Override
    public void handle(LocalProxyConnect local, ProxyMessage message, RemoteProxyConnect remote){
        handleLocalMessage(local, (I) message, remote);
    }


    protected abstract void handleLocalMessage(LocalProxyConnect local, I message, RemoteProxyConnect remote);
}
