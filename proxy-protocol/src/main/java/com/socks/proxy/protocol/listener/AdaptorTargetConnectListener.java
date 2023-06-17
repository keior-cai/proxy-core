package com.socks.proxy.protocol.listener;

import com.socks.proxy.protocol.ServerMiddleProxy;
import com.socks.proxy.protocol.TargetConnect;
import com.socks.proxy.protocol.TargetServer;

/**
 * @author: chuangjie
 * @date: 2023/6/17
 **/
public class AdaptorTargetConnectListener implements TargetConnectListener{
    @Override
    public void onConnected(TargetConnect proxy, TargetServer target){

    }


    @Override
    public void onBinary(ServerMiddleProxy delegate, byte[] bytes){

    }


    @Override
    public void onDisconnected(ServerMiddleProxy delegate){

    }


    @Override
    public void onError(ServerMiddleProxy delegate, Throwable cause){

    }


    @Override
    public void onCallbackError(ServerMiddleProxy delegate, Throwable cause){

    }
}
