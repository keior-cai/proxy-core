package com.socks.proxy.protocol.listener;

import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.TargetServer;

public interface ProxyListener{

    default void onConnect(ProxyConnect connect, TargetServer target){
    }


    default void onCreate(TargetServer target, ProxyConnect connect){
    }


    default void onMessage(ProxyConnect connect, ProxyConnect local, String message){
    }


    default void onMessage(ProxyConnect connect, ProxyConnect local, byte[] binary){
    }


    default void onConnectError(TargetServer target, Throwable throwable){
    }


    default void onClose(ProxyConnect connect, ProxyConnect local){
    }
}
