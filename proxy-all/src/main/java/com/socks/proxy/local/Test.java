package com.socks.proxy.local;

import com.socks.proxy.netty.local.LocalServiceBuilder;
import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.LocalMiddleProxy;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.listener.LocalConnectListener;
import com.socks.proxy.service.DefaultLocalServiceBuilder;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.Collections;

/**
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@Slf4j
public class Test{
    public static void main(String[] args) throws Exception{
        LocalServiceBuilder localServiceBuilder = new DefaultLocalServiceBuilder().setServerList(
                Collections.singletonList(new URI("ws://chuangjie.icu:8041"))).setPort(1082);
        localServiceBuilder.addListener(new LocalConnectListener(){
            @Override
            public void onCreate(LocalConnect local, TargetServer remoteServer, LocalMiddleProxy remote){

            }


            @Override
            public void onConnect(LocalConnect local, TargetServer remoteServer, LocalMiddleProxy remote){

            }


            @Override
            public void onCallbackError(LocalConnect local, LocalMiddleProxy remote, Throwable e){
                log.debug("close 2");
            }


            @Override
            public void onLocalClose(LocalMiddleProxy remote){
                log.debug("close 1");
                remote.close();
            }


            @Override
            public void onError(LocalMiddleProxy connect, Throwable cause){

            }


            @Override
            public void onSendBinary(LocalConnect local, byte[] message, LocalMiddleProxy remote){

            }


            @Override
            public void onBinary(LocalConnect local, byte[] message, LocalMiddleProxy remote){

            }
        }).builder().start();
    }
}
