package com.socks.proxy.protocol.listener;

import com.socks.proxy.protocol.ServerMiddleProxy;
import com.socks.proxy.protocol.TargetConnect;
import com.socks.proxy.protocol.TargetServer;

public interface ServerConnectListener{

    void onConnected(TargetConnect proxy, TargetServer target);


    void onBinary(ServerMiddleProxy delegate, byte[] bytes);


    void onDisconnected(ServerMiddleProxy delegate);


    void onError(ServerMiddleProxy delegate, Throwable cause);


    void onCallbackError(ServerMiddleProxy delegate, Throwable cause);
}
