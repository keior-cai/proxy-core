package com.socks.proxy.protocol.listener;

import com.socks.proxy.protocol.ServerMiddleProxy;

import java.util.Map;

/**
 * <pre>
 *  中间连接监听器,
 *  ss-local -> ss-server
 * </pre>
 */
public interface ServerMiddleMessageListener{

    void onConnect(ServerMiddleProxy proxy, Map<String, String> headers, String s, String selectedSubprotocol);


    void onDisconnect(ServerMiddleProxy proxy);


    void onText(ServerMiddleProxy proxy, String message);


    void onBinary(ServerMiddleProxy proxy, byte[] content);


    void onClose(ServerMiddleProxy proxy, int status, String reason);


    void onCallbackError(ServerMiddleProxy proxy, Throwable cause);


    void onError(ServerMiddleProxy proxy, Throwable cause);
}
