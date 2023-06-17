package com.socks.proxy.handshake;

import com.socks.proxy.protocol.ServerMiddleProxy;
import com.socks.proxy.protocol.listener.ServerMiddleMessageListener;

import java.util.Map;

/**
 * @author: chuangjie
 * @date: 2023/6/5
 **/
public class AdaptorMessageListener implements ServerMiddleMessageListener{

    @Override
    public void onConnect(ServerMiddleProxy proxy, Map<String, String> headers, String s, String selectedSubprotocol){

    }


    @Override
    public void onDisconnect(ServerMiddleProxy proxy){

    }


    @Override
    public void onText(ServerMiddleProxy proxy, String message){

    }


    @Override
    public void onBinary(ServerMiddleProxy proxy, byte[] content){

    }


    @Override
    public void onClose(ServerMiddleProxy proxy, int status, String reason){

    }


    @Override
    public void onCallbackError(ServerMiddleProxy proxy, Throwable cause){

    }


    @Override
    public void onError(ServerMiddleProxy proxy, Throwable cause){

    }
}
