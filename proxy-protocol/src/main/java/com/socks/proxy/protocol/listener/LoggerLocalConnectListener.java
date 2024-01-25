package com.socks.proxy.protocol.listener;

import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.TargetServer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: chuangjie
 * @date: 2023/7/22
 **/
@Slf4j
public class LoggerLocalConnectListener implements ProxyListener{

    @Override
    public void onConnect(ProxyConnect connect, TargetServer target){
        if(log.isDebugEnabled()){
            log.debug("connect = {}, target = {}", connect.channelId(), target);
        }
    }


    @Override
    public void onCreate(TargetServer target, ProxyConnect connect){
        if(log.isDebugEnabled()){
            log.debug("create connect = {}, channelId = {}", target, connect.channelId());
        }
    }


    @Override
    public void onConnectError(TargetServer target, Throwable throwable){
        log.error("fail connect target = {}", target, throwable);
    }

}
