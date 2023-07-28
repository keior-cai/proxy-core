package com.socks.proxy.protocol.listener;

import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.LocalMiddleService;
import com.socks.proxy.protocol.TargetServer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: chuangjie
 * @date: 2023/7/22
 **/
@Slf4j
public class LoggerLocalConnectListener implements LocalConnectListener{
    @Override
    public void onCreate(LocalConnect local, TargetServer remoteServer, LocalMiddleService remote){
        log.debug("CREATE id = {} TARGET = {}:{}", local.channelId(), remoteServer.host(), remoteServer.port());
    }


    @Override
    public void onConnect(LocalConnect local, TargetServer remoteServer, LocalMiddleService remote){

    }


    @Override
    public void onCallbackError(LocalConnect local, LocalMiddleService remote, Throwable e){
        log.error("error LOCAL = {}", local.channelId(), e);
    }


    @Override
    public void onLocalClose(LocalConnect context, LocalMiddleService remote){

    }


    @Override
    public void onError(LocalConnect context, LocalMiddleService connect, Throwable cause){

    }


    @Override
    public void onSendBinary(LocalConnect local, byte[] message, LocalMiddleService remote){

    }


    @Override
    public void onBinary(LocalConnect local, byte[] message, LocalMiddleService remote){

    }


    @Override
    public void onMessage(LocalConnect context, String message, LocalMiddleService localMiddleService){
        log.debug("LOCAL MIDDLE = {} RECEIVE MESSAGE = {}", localMiddleService.channelId(), message);
    }
}
