package com.socks.proxy.protocol.handshake;

import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.LocalMiddleProxy;
import com.socks.proxy.protocol.codes.ProxyMessage;

/**
 * @author: chuangjie
 * @date: 2023/6/11
 **/
@SuppressWarnings("unchecked")
public abstract class SimpleLocalHandshakeMessageHandler<I extends ProxyMessage>
        implements LocalHandshakeMessageHandler{

    @Override
    public void handle(LocalConnect local, ProxyMessage message, LocalMiddleProxy remote){
        handleLocalMessage(local, (I) message, remote);
    }


    protected abstract void handleLocalMessage(LocalConnect local, I message, LocalMiddleProxy remote);
}
