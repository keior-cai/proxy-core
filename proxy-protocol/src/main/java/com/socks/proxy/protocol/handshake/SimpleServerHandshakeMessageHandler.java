package com.socks.proxy.protocol.handshake;

import com.socks.proxy.protocol.ServerMiddleProxy;
import com.socks.proxy.protocol.TargetConnect;
import com.socks.proxy.protocol.codes.ProxyMessage;

/**
 * @author: chuangjie
 * @date: 2023/6/11
 **/
@SuppressWarnings("unchecked")
public abstract class SimpleServerHandshakeMessageHandler<I extends ProxyMessage>
        implements ServerHandshakeMessageHandler{

    @Override
    public void handle(ServerMiddleProxy local, ProxyMessage message, TargetConnect middleProxy){
        handleServerMessage(local, (I) message, middleProxy);
    }


    protected abstract void handleServerMessage(ServerMiddleProxy local, I message, TargetConnect middleProxy);

}
