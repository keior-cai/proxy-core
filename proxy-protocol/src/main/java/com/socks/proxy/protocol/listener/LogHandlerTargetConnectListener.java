package com.socks.proxy.protocol.listener;

import com.socks.proxy.protocol.ServerMiddleProxy;
import com.socks.proxy.protocol.TargetConnect;
import com.socks.proxy.protocol.TargetServer;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 日志连接监听器
 *
 * @author: chuangjie
 * @date: 2023/6/17
 **/
@Slf4j
public class LogHandlerTargetConnectListener implements TargetConnectListener, ServerMiddleMessageListener{
    @Override
    public void onConnected(TargetConnect proxy, TargetServer target){
        log.debug("target proxy created target = {}:{}", target.host(), target.port());
    }


    @Override
    public void onConnect(ServerMiddleProxy proxy, Map<String, String> headers, String s, String selectedSubprotocol){

    }


    @Override
    public void onDisconnect(ServerMiddleProxy proxy){
        log.debug("middle ss-server close connect");
    }


    @Override
    public void onText(ServerMiddleProxy proxy, String message){

    }


    @Override
    public void onBinary(ServerMiddleProxy delegate, byte[] bytes){
    }


    @Override
    public void onClose(ServerMiddleProxy proxy, int status, String reason){
        log.debug("middle ss-server websocket close connect");
    }


    @Override
    public void onDisconnected(ServerMiddleProxy delegate){
        log.debug("target proxy disconnected ");
        delegate.close();
    }


    @Override
    public void onError(ServerMiddleProxy delegate, Throwable cause){
        log.debug("target proxy error", cause);
    }


    @Override
    public void onCallbackError(ServerMiddleProxy delegate, Throwable cause){
        log.debug("target proxy handler error", cause);
    }
}
