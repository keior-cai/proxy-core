package com.socks.proxy.protocol.handshake;

import com.socks.proxy.protocol.codes.ProxyMessage;
import com.socks.proxy.protocol.connect.ProxyConnect;

/**
 * @author: chuangjie
 * @date: 2023/6/11
 **/
@SuppressWarnings("unchecked")
public abstract class SimpleServerHandshakeMessageHandler<I extends ProxyMessage>
        implements ServerHandshakeMessageHandler{

    @Override
    public void handle(ProxyConnect local, ProxyMessage message, ProxyConnect target){
        handleServerMessage(local, (I) message, target);
    }


    protected abstract void handleServerMessage(ProxyConnect local, I message, ProxyConnect target);

}
