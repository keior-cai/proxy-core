package com.socks.proxy.protocol.handshake;

import com.socks.proxy.protocol.RemoteProxyConnect;
import com.socks.proxy.protocol.codes.ProxyMessage;

/**
 * @author: chuangjie
 * @date: 2023/6/11
 **/
@SuppressWarnings("unchecked")
public abstract class SimpleServerHandshakeMessageHandler<I extends ProxyMessage>
        implements ServerHandshakeMessageHandler{

    @Override
    public void handle(RemoteProxyConnect local, ProxyMessage message, RemoteProxyConnect remote){
        handleServerMessage(local, (I) message, remote);
    }


    protected abstract void handleServerMessage(RemoteProxyConnect local, I message, RemoteProxyConnect remote);

}
