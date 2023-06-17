package com.socks.proxy.protocol.listener;

import com.socks.proxy.protocol.ServerMiddleProxy;
import com.socks.proxy.protocol.TargetConnect;
import com.socks.proxy.protocol.TargetServer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: chuangjie
 * @date: 2023/6/17
 **/
@Slf4j
public class LogHandlerTargetConnectListener implements TargetConnectListener{
    @Override
    public void onConnected(TargetConnect proxy, TargetServer target){
        log.debug("target proxy created target = {}:{}", target.host(), target.port());
    }


    @Override
    public void onBinary(ServerMiddleProxy delegate, byte[] bytes){
        log.debug("target proxy send binary size = {}", bytes.length);
    }


    @Override
    public void onDisconnected(ServerMiddleProxy delegate){
        log.debug("target proxy disconnected ");
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
