package com.socks.proxy.protocol.listener;

import com.socks.proxy.protocol.ServerMiddleProxy;
import com.socks.proxy.protocol.TargetConnect;
import com.socks.proxy.protocol.TargetServer;

/**
 * 远程连接监听器，ss-server->target-server连接的事件监听器
 */
public interface TargetConnectListener{

    void onConnected(TargetConnect proxy, TargetServer target);


    void onBinary(ServerMiddleProxy delegate, byte[] bytes);


    void onDisconnected(ServerMiddleProxy delegate);


    void onError(ServerMiddleProxy delegate, Throwable cause);


    void onCallbackError(ServerMiddleProxy delegate, Throwable cause);
}
